package chatchatback.pojo.entity;

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
    private String fullAncientContent;
    private String fullModernContent;
}
