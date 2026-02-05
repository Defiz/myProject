package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vlad.pr.projectCRUD.dto.UserRegistrationDto;
import vlad.pr.projectCRUD.service.RegistrationService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AuthRestController {
    private final RegistrationService registrationService;

    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDto> register(@RequestBody UserRegistrationDto userDto) {
        registrationService.register(userDto);
        return ResponseEntity.ok().build();
    }

}
