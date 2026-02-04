package vlad.pr.telegram_bot_service.dto;

import lombok.Data;

@Data
public class UserTelegramDto {
    private String tgUserName;
    private Long tgChatId;
    private String homeAddress;
    private String jobAddress;
    private String jobTime;
}
