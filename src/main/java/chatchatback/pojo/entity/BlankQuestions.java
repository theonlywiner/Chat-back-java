package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 填空题
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlankQuestions {
    private Long id;
    private String content;
    private String answer;
    private String analysis;
}
