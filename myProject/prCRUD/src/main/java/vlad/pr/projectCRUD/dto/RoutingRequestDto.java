package vlad.pr.projectCRUD.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RoutingRequestDto {
    private List<String> filters = new ArrayList<>();
    private String locale = "ru";
    private List<RoutePointDto> points;
    private String transport = "driving";
    private long utc;

}
