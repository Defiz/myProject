package vlad.pr.projectCRUD.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.pr.projectCRUD.dto.*;
import vlad.pr.projectCRUD.mapper.UserMapper;
import vlad.pr.projectCRUD.model.Role;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.repository.RoleRepository;
import vlad.pr.projectCRUD.repository.UserRepository;
import vlad.pr.projectCRUD.security.UsersDetails;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User nor found!");
        }
        return new UsersDetails(user.get());
    }

    public List<UserListDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserRequestDto getUserById(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toUserRequestDto(user);
    }

    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    public UserProfileDto getUserByName(String name) {
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        return userMapper.toUserProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateUserFromByName(String name, UserProfileDto userDto) {
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        boolean importantChanged = !Objects.equals(user.getUserLocationInfo().getHomeAddress(), userDto.getHomeAddress())
                || !Objects.equals(user.getUserLocationInfo().getJobAddress(), userDto.getJobAddress())
                || !Objects.equals(user.getUserLocationInfo().getJobTime(), userDto.getJobTime());
        if (importantChanged) {
            user.getUserNotification().setNextNotificationUnix(null);
        }
        userMapper.updateUserFromDto(user, userDto);
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(userDto.getPassword());
        }
        User savedUser = userRepository.save(user);
        return userMapper.toUserProfileDto(savedUser);
    }

    @Transactional
    public UserRequestDto createUser(UserRequestDto userDto) {
        User user = userMapper.updateUserFromDto(userDto);
        userRepository.save(user);
        return userMapper.toUserRequestDto(user);
    }

    @Transactional
    public void createOrUpdateUser(TelegramDto userDto, DadataAddressResponseDto home, DadataAddressResponseDto job) {
        User user = userRepository.findByTgChatId(userDto.getTgChatId()).orElseGet(User::new);
        boolean importantChanged = !Objects.equals(user.getUserLocationInfo().getHomeAddress(), userDto.getHomeAddress())
                || !Objects.equals(user.getUserLocationInfo().getJobAddress(), userDto.getJobAddress())
                || !Objects.equals(user.getUserLocationInfo().getJobTime(), userDto.getJobTime());
        if (importantChanged) {
            user.getUserNotification().setNextNotificationUnix(null);
        }
        userMapper.updateUserFromDto(user, userDto, home, job);
        if (user.getRoles() != null && user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByRole("ROLE_USER");
            user.setRoles(Set.of(userRole));
        }
        userRepository.save(user);
    }

    @Transactional
    public UserRequestDto updateUser(Integer id, UserRequestDto userDto) {
        User userFromBase = userRepository.findById(id).orElse(null);
        userFromBase.setName(userDto.getName());
        userFromBase.setAge(userDto.getAge());
        userFromBase.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            userFromBase.setPassword(userDto.getPassword());
        }
        userFromBase.setRoles(userMapper.stringToRole(userDto.getRoles()));
        userRepository.save(userFromBase);
        return userMapper.toUserRequestDto(userFromBase);
    }

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

}
