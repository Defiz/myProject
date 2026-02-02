package vlad.pr.telegram_bot_service.events;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

@AllArgsConstructor
@Component
public class EventsListener {
    private final TelegramClient telegramClient;

    @SneakyThrows
    @EventListener
    public void on(MessageEvent event) {
        BotApiMethod<? extends Serializable> message = event.getMessage();
        telegramClient.execute(message);
    }
}
