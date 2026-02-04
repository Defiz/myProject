package vlad.pr.telegram_bot_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vlad.pr.telegram_bot_service.command.UserStep;
import vlad.pr.telegram_bot_service.dto.UserTelegramDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class UserService {
    private final RestTemplate restTemplate;
    private final Map<Long, UserStep> userSteps = new ConcurrentHashMap<>();
    private final Map<Long, UserTelegramDto> userDto = new ConcurrentHashMap<>();
    @Value("${core.service.url}")
    private String coreServiceUrl;

    public void setStep(long chatId, UserStep step) {
        userSteps.put(chatId, step);
    }

    public UserStep getStep(long chatId) {
        return userSteps.getOrDefault(chatId, UserStep.NONE);
    }

    public UserTelegramDto getOrCreateUser(long chatId, String username) {
        return userDto.computeIfAbsent(chatId, id -> {
            UserTelegramDto dto = new UserTelegramDto();
            dto.setTgChatId(chatId);
            dto.setTgUserName(username);
            return dto;
        });
    }

    public void clearStep(long chatId) {
        userSteps.put(chatId, UserStep.NONE);
    }

    public void sendUserTelegram(UserTelegramDto userDto) {
        restTemplate.postForEntity(coreServiceUrl, userDto, Void.class);
    }

}
