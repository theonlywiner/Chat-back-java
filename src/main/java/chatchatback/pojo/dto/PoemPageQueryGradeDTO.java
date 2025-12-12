package chatchatback.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoemPageQueryGradeDTO extends PageQuery{
    private Long gradeId;
    private String name;
}
