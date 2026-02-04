package vlad.pr.telegram_bot_service.command;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import vlad.pr.telegram_bot_service.dto.UserTelegramDto;
import vlad.pr.telegram_bot_service.events.MessageEvent;
import vlad.pr.telegram_bot_service.service.UserService;

@AllArgsConstructor
@Component
public class TextMessageCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        UserStep step = userService.getStep(update.getMessage().getChatId());
        return UserStep.WAIT_HONE_ADDRESS.equals(step)
                || UserStep.WAIT_JOB_ADDRESS.equals(step)
                || UserStep.WAIT_JOB_TIME.equals(step);
    }

    @Override
    public void handle(Update update) {
        long chatId = update.getMessage().getChatId();
        UserStep step = userService.getStep(chatId);
        UserTelegramDto dto = userService.getOrCreateUser(chatId, update.getMessage().getFrom().getUserName());
        String text = update.getMessage().getText();
        switch (step) {
            case WAIT_HONE_ADDRESS -> {
                dto.setHomeAddress(text);
            }
            case WAIT_JOB_ADDRESS -> {
                dto.setJobAddress(text);
            }
            case WAIT_JOB_TIME -> {
                dto.setJobTime(text);
            }
            default -> {
                return;
            }
        }
        userService.clearStep(chatId);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Сохранено: " + text)
                .build();
        eventPublisher.publishEvent(new MessageEvent(this, message));

    }

    @Override
    public String getCommand() {
        return "text";
    }
}
