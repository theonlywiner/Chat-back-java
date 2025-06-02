package chatchatback.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsHistoryVO {
    private Long id;
    private String sessionId;
    private String username;
    private Integer wordId;
    private String word;
    private String generatedAt;
}
