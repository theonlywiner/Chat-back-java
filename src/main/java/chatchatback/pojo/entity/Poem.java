package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poem{
    private Long id;
    private String name;
    private Long dynastyId;
    private String dynasty;
    private Long authorId;
    private String author;
    private String fullAncientContent;
    private String fullModernContent;
    private String annotation;
    private String appreciation;
    private String background;
    private String peopleAppreciation;
    private List<Integer> links;
}
