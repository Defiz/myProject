package vlad.pr.projectCRUD.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import vlad.pr.projectCRUD.dto.*;
import vlad.pr.projectCRUD.model.Role;
import vlad.pr.projectCRUD.model.User;
import vlad.pr.projectCRUD.util.StringToRoleConvertor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class UserMapper {

    private final StringToRoleConvertor stringToRoleConvertor;

    public List<UserListDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toUserListDto)
                .toList();
    }

    public Set<String> roleToString(Set<Role> roles) {
        return roles.stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
    }

    public Set<Role> stringToRole(Set<String> roles) {
        return roles.stream()
                .map(stringToRoleConvertor::convert)
                .collect(Collectors.toSet());
    }

    public UserListDto toUserListDto(User user) {
        UserListDto userListDto = new UserListDto();
        userListDto.setId(user.getId());
        userListDto.setName(user.getName());
        userListDto.setAge(user.getAge());
        userListDto.setEmail(user.getEmail());
        return userListDto;
    }

    public UserProfileDto toUserProfileDto(User user) {
        UserProfileDto userDto = new UserProfileDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setAge(user.getAge());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(roleToString(user.getRoles()));
        return userDto;
    }

    public User toUser(UserRegistrationDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public UserRegistrationDto toUserRegistrationDto(User user) {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setName(user.getName());
        userDto.setAge(user.getAge());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    public User toUser(UserRequestDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRoles(stringToRole(dto.getRoles()));
        return user;
    }

    public UserRequestDto toUserRequestDto(User user) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setId(user.getId());
        userRequestDto.setName(user.getName());
        userRequestDto.setAge(user.getAge());
        userRequestDto.setEmail(user.getEmail());
        userRequestDto.setPassword(user.getPassword());
        userRequestDto.setRoles(roleToString(user.getRoles()));
        return userRequestDto;
    }

    public User toUser(TelegramDto dto, User existingUser) {
        User user = existingUser != null ? existingUser : new User();
        user.setName(dto.getTgUserName());
        user.setPassword(dto.getTgUserName());
        user.setTgChatId(dto.getTgChatId());
        user.setHomeAddress(dto.getHomeAddress());
        user.setJobAddress(dto.getJobAddress());
        user.setJobTime(dto.getJobTime());
        return user;
    }
}