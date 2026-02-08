package vlad.pr.projectCRUD.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoutingResponseDto {
    private List<RouteDto> result;
}
