package vlad.pr.projectCRUD.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vlad.pr.projectCRUD.dto.RoutePointDto;
import vlad.pr.projectCRUD.dto.RoutingRequestDto;
import vlad.pr.projectCRUD.dto.RoutingResponseDto;
import vlad.pr.projectCRUD.dto.TelegramNotificationDto;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.properties.TwoGisProperties;
import vlad.pr.projectCRUD.repository.UserRepository;
import vlad.pr.projectCRUD.util.DataTimeUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final DataTimeUtil dataTimeUtil;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final TwoGisProperties twoGisProperties;
    @Value("${telegram.service.url}")
    private String telegramServiceUrl;
    @Value("${buffer-time-sec}")
    private long bufferTimeSec;

    @Scheduled(fixedRate = 60_000)
    public void checkNotifications() {
        long now = Instant.now().getEpochSecond();
        List<User> users = userRepository.findAllByNextNotificationUnixIsNullOrNextNotificationUnixLessThanEqual(now);
        for (User user : users) {
            send(user);
            recalculateAndSchedule(user);
        }
    }

    public void recalculateAndSchedule(User user) {
        LocalDate departureDate = resolveDepartureDate(user);
        long departuresUnix = dataTimeUtil.convertToUnix(user.getTimezone(), user.getJobTime(), departureDate);
        long travelTimeSec = resolveTravelTime(user, departuresUnix);
        long leaveUnix = calculateLeaveUnix(departuresUnix, travelTimeSec);
        if (leaveUnix <= Instant.now().getEpochSecond()) {
            LocalDate nextDay = dataTimeUtil.getNextWorkingDay(departureDate.plusDays(1));
            long newDeparturesUnix = dataTimeUtil.convertToUnix(user.getTimezone(), user.getJobTime(), nextDay);
            travelTimeSec = resolveTravelTime(user, newDeparturesUnix);
            leaveUnix = calculateLeaveUnix(newDeparturesUnix, travelTimeSec);
        }
        user.setNextNotificationUnix(leaveUnix);
        userRepository.save(user);
    }

    public LocalDate resolveDepartureDate(User user) {
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDate today = LocalDate.now(offset);
        long now = Instant.now().getEpochSecond();
        if (dataTimeUtil.isWeekend(today)) {
            today = dataTimeUtil.getNextWorkingDay(today);
        }
        long departuresUnix = dataTimeUtil.convertToUnix(user.getTimezone(), user.getJobTime(), today);
        if (departuresUnix <= now) {
            today = dataTimeUtil.getNextWorkingDay(today.plusDays(1));
        }
        return today;
    }

    public long resolveTravelTime(User user, long departuresUnix) {
        long now = Instant.now().getEpochSecond();
        if (user.getNextNotificationUnix() == null) {
            return fetchTravelTime(user.getHomeLon(), user.getHomeLat(), user.getJobLon(), user.getJobLat(), departuresUnix);
        }
        long travelTimeSec = departuresUnix - user.getNextNotificationUnix() - bufferTimeSec;
        long leaveUnix = departuresUnix - travelTimeSec - bufferTimeSec;
        if (leaveUnix <= now) {
            return fetchTravelTime(user.getHomeLon(), user.getHomeLat(), user.getJobLon(), user.getJobLat(), departuresUnix);
        }
        return travelTimeSec;
    }

    public long calculateLeaveUnix(long departuresUnix, long travelTimeSec) {
        return departuresUnix - travelTimeSec - bufferTimeSec;
    }

    public void send(User user) {
        if (user.getNextNotificationUnix() == null) {
            return;
        }
        long notifyUnix = user.getNextNotificationUnix();
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDateTime departuresTime = LocalDateTime.ofEpochSecond(notifyUnix + bufferTimeSec, 0, offset);
        String formattedTime = departuresTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String message = "Через 30 минут нужно выезжать. Точное время выезда: " + formattedTime;
        TelegramNotificationDto userDto = new TelegramNotificationDto(user.getTgChatId(), message);
        restTemplate.postForObject(telegramServiceUrl, userDto, Void.class);
    }

    public long fetchTravelTime(double homeLon, double homeLat, double jobLon, double jobLat, long departureUnix) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        RoutingRequestDto body = new RoutingRequestDto();
        body.setPoints(List.of(new RoutePointDto(homeLon, homeLat),
                new RoutePointDto(jobLon, jobLat)));
        body.setUtc(departureUnix);
        HttpEntity<RoutingRequestDto> request = new HttpEntity<>(body, headers);
        ResponseEntity<RoutingResponseDto> response = restTemplate.postForEntity(twoGisProperties.getUrl(), request, RoutingResponseDto.class);
        return response.getBody().getResult().get(0).getTotal_duration();
    }
}
