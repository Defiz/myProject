package vlad.pr.projectCRUD.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoutePointDto {
    private double lon;
    private double lat;
    private String type = "stop";

    public RoutePointDto(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }
}
