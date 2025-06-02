package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionSession {
    private Long id;
    private Integer userId;
    private String sessionId;
    private Integer wordId;
    private Timestamp generatedAt;
}
