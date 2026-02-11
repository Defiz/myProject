package vlad.pr.projectCRUD.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import vlad.pr.projectCRUD.properties.DadataProperties;
import vlad.pr.projectCRUD.dto.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;

@AllArgsConstructor
@Service
public class GeoService {
    private final UserService userService;
    private final DadataProperties dadataProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public void createUserWithGeoData(TelegramDto userDto) {
        DadataAddressResponseDto home = fetchGeoData(userDto.getHomeAddress());
        DadataAddressResponseDto job = fetchGeoData(userDto.getJobAddress());
        userService.createOrUpdateUser(userDto, home, job);
    }

    @SneakyThrows
    public DadataAddressResponseDto fetchGeoData(String address) {
        String requestBody = objectMapper.writeValueAsString(new String[]{address});
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dadataProperties.getApiUrl()))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Token " + dadataProperties.getToken())
                .header("X-Secret", dadataProperties.getSecretToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        DadataAddressResponseDto[] result = objectMapper.readValue(response.body(), DadataAddressResponseDto[].class);
        return result[0];
    }
}