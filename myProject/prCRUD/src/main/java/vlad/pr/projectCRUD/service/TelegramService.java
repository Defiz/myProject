package vlad.pr.projectCRUD.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.pr.projectCRUD.dto.TelegramAccountDto;
import vlad.pr.projectCRUD.mapper.TelegramMapper;
import vlad.pr.projectCRUD.model.Telegram;
import vlad.pr.projectCRUD.repository.TelegramRepository;

@AllArgsConstructor
@Service
public class TelegramService {

    private final TelegramRepository telegramRepository;
    private final TelegramMapper telegramMapper;

    @Transactional
    public void createUser(TelegramAccountDto userDto) {
        Telegram telegram = telegramMapper.toTelegram(userDto);
        telegramRepository.save(telegram);
    }
}
