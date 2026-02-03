package vlad.pr.telegram_bot_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import vlad.pr.telegram_bot_service.command.Command;

import java.util.Collection;

@AllArgsConstructor
@Service
public class CommandHandler {
    private final Collection<Command> commands;

    public void handle(Update update) {
        for(Command command : commands) {
            if (command.canHandle(update)) {
                command.handle(update);
                return;
            }
        }
    }
}

