package vlad.pr.projectCRUD.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import vlad.pr.projectCRUD.dto.RoutePointDto;
import vlad.pr.projectCRUD.dto.RoutingRequestDto;
import vlad.pr.projectCRUD.dto.RoutingResponseDto;
import vlad.pr.projectCRUD.dto.TelegramNotificationDto;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.properties.TwoGisProperties;
import vlad.pr.projectCRUD.repository.UserRepository;
import vlad.pr.projectCRUD.util.DataTimeUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final DataTimeUtil dataTimeUtil;
    private final UserRepository userRepository;
    private final TwoGisProperties twoGisProperties;
    @Value("${telegram.service.url}")
    private String telegramServiceUrl;
    @Value("${buffer-time-sec}")
    private long bufferTimeSec;

    @Scheduled(fixedRate = 60_000)
    public void checkNotifications() {
        long now = Instant.now().getEpochSecond();
        List<User> users = userRepository.findAllUsersWithNotificationDue(now);
        for (User user : users) {
            send(user);
            recalculateAndSchedule(user, now);
        }
    }

    public void recalculateAndSchedule(User user, long now) {
        LocalDate departureDate = resolveDepartureDate(user, now);
        long departuresUnix = dataTimeUtil.convertToUnix(user.getTimezone(), user.getUserLocationInfo().getJobTime(), departureDate);
        long travelTimeSec = resolveTravelTime(user, departuresUnix, now);
        long leaveUnix = calculateLeaveUnix(departuresUnix, travelTimeSec);
        if (leaveUnix <= now) {
            LocalDate nextDay = dataTimeUtil.getNextWorkingDay(departureDate.plusDays(1));
            long newDeparturesUnix = dataTimeUtil.convertToUnix(user.getTimezone(), user.getUserLocationInfo().getJobTime(), nextDay);
            travelTimeSec = resolveTravelTime(user, newDeparturesUnix, now);
            leaveUnix = calculateLeaveUnix(newDeparturesUnix, travelTimeSec);
        }
        user.getUserNotification().setNextNotificationUnix(leaveUnix);
        userRepository.save(user);
    }

    public LocalDate resolveDepartureDate(User user, long now) {
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDate today = LocalDate.now(offset);
        if (dataTimeUtil.isWeekend(today)) {
            today = dataTimeUtil.getNextWorkingDay(today);
        }
        long departuresUnix = dataTimeUtil.convertToUnix(user.getTimezone(), user.getUserLocationInfo().getJobTime(), today);
        if (departuresUnix <= now) {
            today = dataTimeUtil.getNextWorkingDay(today.plusDays(1));
        }
        return today;
    }

    public long resolveTravelTime(User user, long departuresUnix, long now) {
        if (user.getUserNotification().getNextNotificationUnix() == null) {
            return fetchTravelTime(user.getUserLocationInfo().getHomeLon(), user.getUserLocationInfo().getHomeLat(), user.getUserLocationInfo().getJobLon(), user.getUserLocationInfo().getJobLat(), departuresUnix);
        }
        long travelTimeSec = departuresUnix - user.getUserNotification().getNextNotificationUnix() - bufferTimeSec;
        long leaveUnix = departuresUnix - travelTimeSec - bufferTimeSec;
        if (leaveUnix <= now) {
            return fetchTravelTime(user.getUserLocationInfo().getHomeLon(), user.getUserLocationInfo().getHomeLat(), user.getUserLocationInfo().getJobLon(), user.getUserLocationInfo().getJobLat(), departuresUnix);
        }
        return travelTimeSec;
    }

    public long calculateLeaveUnix(long departuresUnix, long travelTimeSec) {
        return departuresUnix - travelTimeSec - bufferTimeSec;
    }

    @SneakyThrows
    public void send(User user) {
        if (user.getUserNotification().getNextNotificationUnix() == null) {
            return;
        }
        long notifyUnix = user.getUserNotification().getNextNotificationUnix();
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDateTime departuresTime = LocalDateTime.ofEpochSecond(notifyUnix + bufferTimeSec, 0, offset);
        String formattedTime = departuresTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String message = "Через 30 минут нужно выезжать. Точное время выезда: " + formattedTime;
        TelegramNotificationDto userDto = new TelegramNotificationDto(user.getTgChatId(), message);
        String requestBody = objectMapper.writeValueAsString(userDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(telegramServiceUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @SneakyThrows
    public long fetchTravelTime(double homeLon, double homeLat, double jobLon, double jobLat, long departureUnix) {
        RoutingRequestDto body = new RoutingRequestDto();
        body.setPoints(List.of(new RoutePointDto(homeLon, homeLat),
                new RoutePointDto(jobLon, jobLat)));
        body.setUtc(departureUnix);
        String requestBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(twoGisProperties.getUrl()))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        RoutingResponseDto routingResponse = objectMapper.readValue(response.body(), RoutingResponseDto.class);
        return routingResponse.getResult().get(0).getTotal_duration();
    }
}
