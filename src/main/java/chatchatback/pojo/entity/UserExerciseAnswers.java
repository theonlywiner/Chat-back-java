package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExerciseAnswers {
    private Long id;
    private Long userId;
    private Long questionId;
    /** manual or ai */
    private String questionType;
    private String userAnswer;
    /** 0 or 1 */
    private Integer isCorrect;
    private LocalDateTime createdAt;
}