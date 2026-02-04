package vlad.pr.telegram_bot_service.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandName {
    START("START_COMMAND");

    private final String name;
    }
