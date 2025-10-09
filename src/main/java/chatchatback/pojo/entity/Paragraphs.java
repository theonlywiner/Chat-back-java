package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paragraphs {
    private Long id;
    private Long chapterId;
    private String ancientText;
    private String modernText;
}
