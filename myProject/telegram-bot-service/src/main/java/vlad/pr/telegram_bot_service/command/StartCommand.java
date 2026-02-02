package vlad.pr.telegram_bot_service.command;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import vlad.pr.telegram_bot_service.events.MessageEvent;

@AllArgsConstructor
@Component
public class StartCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().equals("/start");
    }

    @SneakyThrows
    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Привет")
                    .build();
            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }

    @Override
    public String getCommand() {
        return CommandName.START.getName();
    }
}
