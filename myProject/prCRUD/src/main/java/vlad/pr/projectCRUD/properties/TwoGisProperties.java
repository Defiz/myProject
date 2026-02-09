package vlad.pr.projectCRUD.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "two-gis")
public class TwoGisProperties {
    private String url;
}
