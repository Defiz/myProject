package vlad.pr.telegram_bot_service.command;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import vlad.pr.telegram_bot_service.events.MessageEvent;
import vlad.pr.telegram_bot_service.service.KeyboardService;
import vlad.pr.telegram_bot_service.service.UserService;

@AllArgsConstructor
@Component
public class StartCallbackCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        String callbackData = update.getCallbackQuery().getData();
        return KeyboardService.HOME_ADDRESS.equals(callbackData)
                || KeyboardService.JOB_ADDRESS.equals(callbackData)
                || KeyboardService.JOB_TIME.equals(callbackData);
    }

    @Override
    public void handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callbackData = update.getCallbackQuery().getData();
        switch (callbackData) {
            case KeyboardService.HOME_ADDRESS -> {
                userService.setStep(chatId, UserStep.WAIT_HONE_ADDRESS);
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите домашний адрес:")
                        .build();
                eventPublisher.publishEvent(new MessageEvent(this, message));
            }
            case KeyboardService.JOB_ADDRESS -> {
                userService.setStep(chatId, UserStep.WAIT_JOB_ADDRESS);
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите адрес работы:")
                        .build();
                eventPublisher.publishEvent(new MessageEvent(this, message));
            }
            case KeyboardService.JOB_TIME -> {
                userService.setStep(chatId, UserStep.WAIT_JOB_TIME);
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите время начало работы:")
                        .build();
                eventPublisher.publishEvent(new MessageEvent(this, message));
            }
        }
    }

    @Override
    public String getCommand() {
        return CommandName.START.getName();
    }
}

