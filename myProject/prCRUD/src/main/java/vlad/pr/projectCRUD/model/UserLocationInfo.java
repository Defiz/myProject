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
@Table(name = "user_location_info")
public class UserLocationInfo {
    @Id
    private Integer id;
    @Column
    private String homeAddress;
    @Column
    private String jobAddress;
    @Column
    private String jobTime;
    @Column
    private double homeLat;
    @Column
    private double homeLon;
    @Column
    private double jobLat;
    @Column
    private double jobLon;
    @MapsId
    @OneToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;
}
