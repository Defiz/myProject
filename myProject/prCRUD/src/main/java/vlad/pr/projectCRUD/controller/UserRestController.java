package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad.pr.projectCRUD.dto.UserProfileDto;
import vlad.pr.projectCRUD.security.UsersDetails;
import vlad.pr.projectCRUD.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public UserProfileDto getUser(@AuthenticationPrincipal UsersDetails usersDetails) {
        return userService.getUserByName(usersDetails.getUsername());
    }
}
