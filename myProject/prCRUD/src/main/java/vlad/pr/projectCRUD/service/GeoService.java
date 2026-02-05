package vlad.pr.projectCRUD.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vlad.pr.projectCRUD.config.DadataProperties;
import vlad.pr.projectCRUD.dto.DadataAddressResponseDto;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class GeoService {
    private final RestTemplate restTemplate;
    private final DadataProperties dadataProperties;

    public String fetchTimeZone(String homeAddress) {
        String[] body = new String[]{homeAddress};
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + dadataProperties.getToken());
        headers.set("X-Secret", dadataProperties.getSecretToken());
        HttpEntity<String[]> request = new HttpEntity<>(body, headers);
        DadataAddressResponseDto[] response = restTemplate.postForObject(dadataProperties.getApiUrl(), request, DadataAddressResponseDto[].class);
        return response[0].getTimezone();
    }
}
