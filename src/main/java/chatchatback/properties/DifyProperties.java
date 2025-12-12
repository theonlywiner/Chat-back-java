package chatchatback.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatchat.dify.api")
@Data
public class DifyProperties {
    private String baseUrl;
    private String apiKey;

    private String apiTest;
}
