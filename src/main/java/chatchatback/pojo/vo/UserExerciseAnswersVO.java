package chatchatback.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExerciseAnswersVO {
    private Long id;
    private Long userId;
    private Long questionId;

    //题目来源
    private String sourceText;
    //题目内容
    private String content;
    //用户回答
    private String answer;
    //分析
    private String analysis;
    //难易度
    private Integer difficulty;
    //技巧id
    private BigInteger techniqueId;

    /** manual or ai */
    private String questionType;
    private String userAnswer;
    /** 0 or 1 */
    private Integer isCorrect;
    private LocalDateTime createdAt;
}