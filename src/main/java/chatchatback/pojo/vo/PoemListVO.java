package chatchatback.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoemListVO{
    private Long id;
    private String name;
    private Long dynastyId;
    private String dynasty;
    private Long authorId;
    private String author;
    //古诗对应的年级id
    private Integer gradeId;
    //古诗对应的年级
    private String grade;
    //古诗对应的课文中的页码
    private Integer page;
    private String fullAncientContent;
    private String fullModernContent;
}
