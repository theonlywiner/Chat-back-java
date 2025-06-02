package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选择题
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceQuestions {
    private Long id;
    private String content;
    private String[] options;
    private Integer answer;
    private String analysis;
}
