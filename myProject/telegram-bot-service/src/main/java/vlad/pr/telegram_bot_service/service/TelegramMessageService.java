package vlad.pr.telegram_bot_service.service;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import vlad.pr.telegram_bot_service.dto.TelegramNotificationDto;
import vlad.pr.telegram_bot_service.events.MessageEvent;

@AllArgsConstructor
@Service
public class TelegramMessageService {
    private final ApplicationEventPublisher eventPublisher;

    public void sendNotification(TelegramNotificationDto userDto) {
        SendMessage message = SendMessage.builder()
                .chatId(Long.valueOf(userDto.getTgChatId()))
                .text(userDto.getMessage())
                .build();
        eventPublisher.publishEvent(new MessageEvent(this, message));
    }
}
