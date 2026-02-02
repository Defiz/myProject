package vlad.pr.telegram_bot_service.command;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class StartCommand implements Command {
    private final TelegramClient telegramClient;

    public StartCommand(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

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
            telegramClient.execute(message);
        }
    }

    @Override
    public String getCommand() {
        return CommandName.START.getName();
    }
}
