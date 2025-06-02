package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 词义
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeaningsPoems {
    private Long id;
    private String meaning;
    private String type;
    private String subtype;
    private Long wordId;

    //每个翻译对应的多个例子
    private List<ExamplesPoems> examplesPoemsList;
}
