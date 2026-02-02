package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vlad.pr.projectCRUD.dto.UserListDto;
import vlad.pr.projectCRUD.dto.UserRequestDto;
import vlad.pr.projectCRUD.service.UserService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
public class AdminRestController {

    private final UserService userService;

    @GetMapping
    public List<UserListDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserRequestDto getUser(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserRequestDto> saveUser(@RequestBody UserRequestDto userDto) {
        UserRequestDto userCreate = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreate);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserRequestDto> editUser(@PathVariable Integer id, @RequestBody UserRequestDto userDto) {
        UserRequestDto userUpdate = userService.updateUser(id, userDto);
        return ResponseEntity.ok(userUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
