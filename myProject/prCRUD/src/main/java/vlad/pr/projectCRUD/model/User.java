package vlad.pr.projectCRUD.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String name;
    @Column
    private int age;
    @Column
    private String email;
    @Column
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @Column
    private Integer tgChatId;
    @Column
    private String tgUserName;
    @Column
    private String timezone;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserLocationInfo userLocationInfo;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserNotification userNotification;
}
