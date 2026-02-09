package vlad.pr.projectCRUD.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String name;
    private int age;
    private String email;
    private String password;
    private String homeAddress;
    private String jobAddress;
    private String jobTime;
}
