package vlad.pr.projectCRUD.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vlad.pr.projectCRUD.dto.UserRegistrationDto;
import vlad.pr.projectCRUD.service.UserService;

@AllArgsConstructor
@Component
public class UserValidator implements Validator {
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationDto userDto = (UserRegistrationDto) target;
      if (userService.existsByName(userDto.getName())) {
          errors.rejectValue("name", "" , "Пользователь с таким именем уже существует");
      }
    }
}
