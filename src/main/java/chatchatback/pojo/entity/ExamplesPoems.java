package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 例句
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamplesPoems {
    private Long id;
    private Long meaningId;
    private String example;
    private String source;
    private String grade;
    private Long poemId;
}
