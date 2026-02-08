package vlad.pr.telegram_bot_service.command;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import vlad.pr.telegram_bot_service.events.MessageEvent;
import vlad.pr.telegram_bot_service.service.KeyboardService;


@AllArgsConstructor
@Component
public class StartCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    private final KeyboardService keyboardService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().equals("/start");
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Привет! Выбери действие:")
                    .replyMarkup(keyboardService.mainMenu())
                    .build();
            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }
}
