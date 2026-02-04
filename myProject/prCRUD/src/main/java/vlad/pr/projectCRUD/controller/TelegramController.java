package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad.pr.projectCRUD.dto.TelegramAccountDto;
import vlad.pr.projectCRUD.service.TelegramService;
import vlad.pr.projectCRUD.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    private final TelegramService telegramService;

    public ResponseEntity<Void> saveTelegramUser(@RequestBody TelegramAccountDto userDto) {
        telegramService.createUser(userDto);
        return ResponseEntity.ok().build();
    }
}
