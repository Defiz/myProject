package vlad.pr.projectCRUD.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadataAddressResponseDto {
    private String timezone;
}
