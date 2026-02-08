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
import tools.jackson.databind.ObjectMapper;
import vlad.pr.projectCRUD.config.DadataProperties;
import vlad.pr.projectCRUD.config.TwoGisProperties;
import vlad.pr.projectCRUD.dto.*;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.repository.UserRepository;

import java.time.*;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GeoService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final DadataProperties dadataProperties;
    private final TwoGisProperties twoGisProperties;
    @Value("${telegram.service.url}")
    private String telegramServiceUrl;

    public void createUserWithGeoData(TelegramDto userDto) {
        DadataAddressResponseDto home = fetchGeoData(userDto.getHomeAddress());
        DadataAddressResponseDto job = fetchGeoData(userDto.getJobAddress());
        userService.createOrUpdateUser(userDto, home, job);
    }

    public DadataAddressResponseDto fetchGeoData(String address) {
        String[] body = new String[]{address};
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + dadataProperties.getToken());
        headers.set("X-Secret", dadataProperties.getSecretToken());
        HttpEntity<String[]> request = new HttpEntity<>(body, headers);
        DadataAddressResponseDto[] response = restTemplate.postForObject(dadataProperties.getApiUrl(), request, DadataAddressResponseDto[].class);
        return response[0];
    }

    @Scheduled(fixedRate = 60_000)
    public void checkUsersForNotifications() {
        long timeNow = Instant.now().getEpochSecond();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            ensureRouteCalculated(user);
            if (shouldSendNotification(user, timeNow)) {
                send(user);
                ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
                LocalDate today = LocalDate.now(offset);
                userService.markUserNotified(user.getId(), today);
            }
        }
    }

    public void ensureRouteCalculated(User user) {
        int bufferTimeSec = 1800;
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDate today = LocalDate.now(offset);
        if (user.getRouteCalculatedDate() != null && user.getRouteCalculatedDate().equals(today)) {
            return;
        }
        DayOfWeek day = today.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            user.setRouteCalculatedDate(today);
            user.setLeaveUnix(null);
            user.setTravelTimeSec(null);
            userRepository.save(user);
            return;
        }
        long departuresUnix = convertToUnix(user.getTimezone(), user.getJobTime());
        long travelTimeSec = fetchTravelTime(user.getHomeLon(), user.getHomeLat(), user.getJobLon(), user.getJobLat(), departuresUnix);
        long leaveUnix = departuresUnix - travelTimeSec - bufferTimeSec;
        user.setTravelTimeSec(travelTimeSec);
        user.setLeaveUnix(leaveUnix);
        user.setRouteCalculatedDate(today);
        userRepository.save(user);
    }

    public boolean shouldSendNotification(User user, long now) {
        if (user.getLeaveUnix() == null) {
            return false;
        }
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDate today = LocalDate.now(offset);
        if (today.equals(user.getLastNotificationDate())) {
            return false;
        }
        return now >= user.getLeaveUnix();
    }

    public void send(User user) {
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDateTime leaveDateTime = LocalDateTime.ofEpochSecond(user.getLeaveUnix(), 0, offset);
        String message = String.format("Пора выезжать в %02d:%02d.\nВремя в пути: %d минут.",
                leaveDateTime.getHour(),
                leaveDateTime.getMinute(),
                user.getTravelTimeSec() / 60);
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
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(body);
        System.out.println("JSON: " + requestBody);
        HttpEntity<RoutingRequestDto> request = new HttpEntity<>(body, headers);
        ResponseEntity<RoutingResponseDto> response = restTemplate.postForEntity(twoGisProperties.getUrl(), request, RoutingResponseDto.class);
        return response.getBody().getResult().get(0).getTotal_duration();
    }

    public long convertToUnix(String timezone, String jobTime) {
        LocalTime clientTime = LocalTime.parse(jobTime);
        ZoneOffset offset = ZoneOffset.of(timezone.replace("UTC", ""));
        OffsetDateTime nowClient = OffsetDateTime.now(offset);
        LocalDate todayClient = nowClient.toLocalDate();
        LocalDateTime clientDataTime = LocalDateTime.of(todayClient, clientTime);
        OffsetDateTime clientOffsetDataTime = clientDataTime.atOffset(offset);
        if (!clientOffsetDataTime.isAfter(nowClient)) {
            LocalDate tomorrowClient = todayClient.plusDays(1);
            clientDataTime = LocalDateTime.of(tomorrowClient, clientTime);
            clientOffsetDataTime = clientDataTime.atOffset(offset);
        }
        return clientOffsetDataTime.toEpochSecond();
    }
}
