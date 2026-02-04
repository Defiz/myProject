package vlad.pr.telegram_bot_service.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardService {
    public static final String HOME_ADDRESS = "home";
    public static final String JOB_ADDRESS = "job";
    public static final String JOB_TIME = "time";

    public ReplyKeyboard mainMenu() {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Адрес дома")
                .callbackData(HOME_ADDRESS)
                .build()));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Адрес работы")
                .callbackData(JOB_ADDRESS)
                .build()));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Время начало работы")
                .callbackData(JOB_TIME)
                .build()));
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rows)
                .build();
    }
}
