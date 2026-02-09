package vlad.pr.projectCRUD.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vlad.pr.projectCRUD.properties.DadataProperties;
import vlad.pr.projectCRUD.dto.*;

import java.time.*;
import java.util.Collections;

@AllArgsConstructor
@Service
public class GeoService {
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final DadataProperties dadataProperties;

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
}