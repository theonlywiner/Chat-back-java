package chatchatback.pojo.dto;

import lombok.Data;

@Data
public class UserExerciseAnswerDTO {
    private Long questionId;
    private String questionType; // manual or ai
    private String userAnswer;
    private Integer isCorrect; // 0 or 1
}
