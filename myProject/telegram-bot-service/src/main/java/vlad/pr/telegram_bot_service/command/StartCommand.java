package vlad.pr.telegram_bot_service.command;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vlad.pr.telegram_bot_service.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

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
                    .text("Привет! Выбери действие:")
                    .replyMarkup(userData())
                    .build();
            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }

    private ReplyKeyboard userData() {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Адрес дома")
                .callbackData("home")
                .build()));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Адрес работы")
                .callbackData("work")
                .build()));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Время начало работы")
                .callbackData("time")
                .build()));
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rows)
                .build();
    }

    @Override
    public String getCommand() {
        return CommandName.START.getName();
    }
}
