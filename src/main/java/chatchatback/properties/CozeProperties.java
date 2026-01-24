package chatchatback.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatchat.coze")
@Data
public class CozeProperties {
    private String apiUrl;
    private String token;
    private String workflowId;
}
