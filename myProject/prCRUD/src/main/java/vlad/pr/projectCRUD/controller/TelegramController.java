package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad.pr.projectCRUD.dto.TelegramDto;
import vlad.pr.projectCRUD.service.GeoService;
import vlad.pr.projectCRUD.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {
    private final GeoService geoService;

    @PostMapping
    public ResponseEntity<Void> saveTelegramUser(@RequestBody TelegramDto userDto) {
        geoService.createUserWithTimezone(userDto);
        return ResponseEntity.ok().build();
    }
}
