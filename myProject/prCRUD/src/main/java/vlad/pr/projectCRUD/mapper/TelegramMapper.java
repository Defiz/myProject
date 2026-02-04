package vlad.pr.projectCRUD.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import vlad.pr.projectCRUD.dto.TelegramDto;
import vlad.pr.projectCRUD.model.Telegram;

@AllArgsConstructor
@Component
public class TelegramMapper {

    public Telegram toTelegram(TelegramDto dto) {
        Telegram telegram = new Telegram();
        telegram.setTgUserName(dto.getTgUserName());
        telegram.setTgChatId(dto.getTgChatId());
        telegram.setHomeAddress(dto.getHomeAddress());
        telegram.setJobAddress(dto.getJobAddress());
        telegram.setJobTime(dto.getJobTime());
        return telegram;
    }
}
