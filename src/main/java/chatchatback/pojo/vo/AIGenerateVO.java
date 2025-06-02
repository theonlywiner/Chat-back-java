package chatchatback.pojo.vo;

import chatchatback.pojo.entity.BlankQuestions;
import chatchatback.pojo.entity.ChoiceQuestions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIGenerateVO {
    private Integer id;
    private String word;

    private List<ChoiceQuestions> choiceQuestionsList;
    private List<BlankQuestions> blankQuestionsList;
}
