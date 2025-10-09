package chatchatback.pojo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import chatchatback.pojo.enums.DifficultyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//'习题题库表'
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseQuestions {
    private Long id;
    @JsonProperty("source_text")
    private String sourceText;
    private String content;
    private String answer;
    private String analysis;
    private List<String> positions;
    private DifficultyStatus difficulty;
}
