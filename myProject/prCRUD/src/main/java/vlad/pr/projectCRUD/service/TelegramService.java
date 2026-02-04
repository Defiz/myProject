package vlad.pr.projectCRUD.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.pr.projectCRUD.dto.TelegramDto;
import vlad.pr.projectCRUD.mapper.UserMapper;
import vlad.pr.projectCRUD.model.Role;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.repository.RoleRepository;
import vlad.pr.projectCRUD.repository.UserRepository;

import java.util.Set;

@AllArgsConstructor
@Service
public class TelegramService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void createUser(TelegramDto userDto) {
        User existingUser = userRepository.findByTgChatId(userDto.getTgChatId());
        User user = userMapper.toUser(userDto, existingUser);
        if (user.getRoles() != null && user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByRole("ROLE_USER");
            user.setRoles(Set.of(userRole));
        }
        userRepository.save(user);
    }
}
