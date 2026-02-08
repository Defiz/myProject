package vlad.pr.telegram_bot_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad.pr.telegram_bot_service.dto.TelegramNotificationDto;
import vlad.pr.telegram_bot_service.service.TelegramMessageService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/core")
public class CoreController {
    private final TelegramMessageService telegramMessageService;

    @PostMapping
    public ResponseEntity<Void> sendMessageTelegramUser(@RequestBody TelegramNotificationDto userDto) {
        System.out.println("Telegram service received DTO: " + userDto);
        telegramMessageService.sendNotification(userDto);
        return ResponseEntity.ok().build();
    }
}
