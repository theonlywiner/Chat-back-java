package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedChoiceQuestion {
    private Long id;
    private String sessionId;
    private String content;
    private String options; // JSON字符串，建议用List<String>时加类型处理器
    private Integer answer;
    private String analysis;
}
