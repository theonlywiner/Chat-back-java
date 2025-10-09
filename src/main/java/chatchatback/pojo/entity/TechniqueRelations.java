package chatchatback.pojo.entity;

import lombok.Builder;
import lombok.Data;

//'技巧与题目的关联表'
@Data
@Builder
public class TechniqueRelations {
    private Long techniqueId;
    private Long questionId;
}
