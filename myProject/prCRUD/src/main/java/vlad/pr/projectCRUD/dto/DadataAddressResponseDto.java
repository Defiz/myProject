package vlad.pr.projectCRUD.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadataAddressResponseDto {
    @JsonProperty("geo_lat")
    private double geoLat;
    @JsonProperty("geo_lon")
    private double geoLon;
    private String timezone;
}
