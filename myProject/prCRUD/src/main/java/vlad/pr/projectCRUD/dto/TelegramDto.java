package vlad.pr.projectCRUD.dto;

import lombok.Data;

@Data
public class TelegramDto {
    private String tgUserName;
    private Integer tgChatId;
    private String homeAddress;
    private String jobAddress;
    private String jobTime;
}