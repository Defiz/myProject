package vlad.pr.telegram_bot_service.service;

import org.springframework.stereotype.Service;
import vlad.pr.telegram_bot_service.command.UserStep;
import vlad.pr.telegram_bot_service.dto.UserTelegramDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<Long, UserStep> userSteps = new ConcurrentHashMap<>();
    private final Map<Long, UserTelegramDto> userDto = new ConcurrentHashMap<>();

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
}
