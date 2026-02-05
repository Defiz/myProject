package vlad.pr.projectCRUD.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dadata")
public class DadataProperties {
    private String token;
    private String secretToken;
    private String apiUrl;
}
