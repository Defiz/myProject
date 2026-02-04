package vlad.pr.projectCRUD.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "telegram")
public class Telegram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tgUserName")
    private String tgUserName;

    @Column(name = "tgChatId")
    private Integer tgChatId;

    @Column(name = "homeAddress")
    private String homeAddress;

    @Column(name = "jobAddress")
    private String jobAddress;

    @Column(name = "jobTime")
    private String jobTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
