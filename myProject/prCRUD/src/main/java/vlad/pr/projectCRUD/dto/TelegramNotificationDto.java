package vlad.pr.projectCRUD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelegramNotificationDto {
    private Integer tgChatId;
    private String message;
}
