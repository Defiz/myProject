package vlad.pr.projectCRUD.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    private int age;
    private String email;
    private String password;
}

