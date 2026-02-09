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
import java.time.format.DateTimeFormatter;
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
    @Value("${buffer-time-sec}")
    private long bufferTimeSec;

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
    public void checkNotifications() {
        long now = Instant.now().getEpochSecond();
        List<User> users = userRepository.findAllByNextNotificationUnixIsNullOrNextNotificationUnixLessThanEqual(now);
        for (User user : users) {
            send(user);
            recalculateAndSchedule(user);
        }
    }

    public void recalculateAndSchedule(User user) {
        ZoneOffset offset = ZoneOffset.of(user.getTimezone().replace("UTC", ""));
        LocalDate today = LocalDate.now(offset);
        long now = Instant.now().getEpochSecond();
        if (isWeekend(today)) {
            today = getNextWorkingDay(today);
        }
        long departuresUnix = convertToUnix(user.getTimezone(), user.getJobTime(), today);
        long travelTimeSec;
        if (user.getNextNotificationUnix() == null) {
            if (departuresUnix <= now) {
                today = getNextWorkingDay(today.plusDays(1));
                departuresUnix = convertToUnix(user.getTimezone(), user.getJobTime(), today);
            }
            travelTimeSec = fetchTravelTime(user.getHomeLon(), user.getHomeLat(), user.getJobLon(), user.getJobLat(), departuresUnix);
        } else {
            travelTimeSec = departuresUnix - user.getNextNotificationUnix() - bufferTimeSec;
            long leaveUnix = departuresUnix - travelTimeSec - bufferTimeSec;
            if (leaveUnix <= now) {
                today = getNextWorkingDay(today.plusDays(1));
                departuresUnix = convertToUnix(user.getTimezone(), user.getJobTime(), today);
                travelTimeSec = fetchTravelTime(user.getHomeLon(), user.getHomeLat(), user.getJobLon(), user.getJobLat(), departuresUnix);
            }
        }
        long leaveUnix = departuresUnix - travelTimeSec - bufferTimeSec;
        user.setNextNotificationUnix(leaveUnix);
        userRepository.save(user);
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
        System.out.println(userDto);
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

    public LocalDate getNextWorkingDay(LocalDate date) {
        LocalDate result = date;
        while (isWeekend(result)) {
            result = result.plusDays(1);
        }
        return result;
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public long convertToUnix(String timezone, String jobTime, LocalDate date) {
        LocalTime clientTime = LocalTime.parse(jobTime);
        ZoneOffset offset = ZoneOffset.of(timezone.replace("UTC", ""));
        LocalDateTime clientDateTime = LocalDateTime.of(date, clientTime);
        OffsetDateTime clientOffsetDateTime = clientDateTime.atOffset(offset);
        return clientOffsetDateTime.toEpochSecond();
    }
}