package vlad.pr.projectCRUD.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserRequestDto {
    private Integer id;
    private String name;
    private int age;
    private String email;
    private String password;
    private Set<String> roles;
}
