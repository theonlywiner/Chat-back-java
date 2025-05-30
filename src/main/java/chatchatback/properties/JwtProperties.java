package chatchatback.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatchat.jwt")
@Data
public class JwtProperties {
    /**
     * 令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;
}
