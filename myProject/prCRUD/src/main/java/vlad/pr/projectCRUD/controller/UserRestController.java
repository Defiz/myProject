package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    @PatchMapping
    public UserProfileDto updateUser(@AuthenticationPrincipal UsersDetails usersDetails, @RequestBody UserProfileDto userProfileDto) {
        return userService.updateUserFromByName(usersDetails.getUsername(), userProfileDto);
    }
}
