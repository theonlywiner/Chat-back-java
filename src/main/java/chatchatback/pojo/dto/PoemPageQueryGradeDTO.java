package chatchatback.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoemPageQueryGradeDTO {
    private Integer page = 1;
    private Integer pageSize = 5;
    private Long gradeId;
    private String name;
}
