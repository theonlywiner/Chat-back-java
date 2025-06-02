package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedBlankQuestion {
    private Long id;
    private String sessionId;
    private String content;
    private String answer;
    private String analysis;
}
