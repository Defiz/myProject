package vlad.pr.projectCRUD.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vlad.pr.projectCRUD.dto.UserRegistrationDto;
import vlad.pr.projectCRUD.mapper.UserMapper;
import vlad.pr.projectCRUD.model.Role;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.repository.RoleRepository;
import vlad.pr.projectCRUD.repository.UserRepository;

import java.util.Set;

@AllArgsConstructor
@Service
public class RegistrationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserRegistrationDto register(UserRegistrationDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Имя уже занято");
        }
        User user = userMapper.toUser(userDto);
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return userMapper.toUserRegistrationDto(user);
    }
}
