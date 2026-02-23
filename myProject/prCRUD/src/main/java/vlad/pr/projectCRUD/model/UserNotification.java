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
@Table(name = "user_notification")
public class UserNotification {
    @Id
    private Integer id;
    @Column
    private Long nextNotificationUnix;
    @MapsId
    @OneToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;;
}
