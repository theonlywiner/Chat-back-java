package chatchatback.pojo.vo;

import chatchatback.pojo.entity.WordCloudItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorWordCloudVO {
    private String author;
    private String dynasty;
    private List<WordCloudItem> wordCloudItemList;
}
