package chatchatback.pojo.vo;

import chatchatback.pojo.entity.ExerciseQuestions;
import chatchatback.pojo.enums.DifficultyStatus;
import chatchatback.pojo.entity.Step;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentenceQuestionVO {
    private Long id;
    private String name;
    private String description;
    private DifficultyStatus difficulty;
    private String keywords;    //标志词（逗号分隔）
    private String pitfalls;    //易错点（逗号分隔）

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Step> steps;   //操作步骤

    List<ExerciseQuestions> exerciseQuestionsList;
}