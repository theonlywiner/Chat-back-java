package chatchatback.pojo.entity;

import chatchatback.pojo.enums.PriorityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordsPoems {
    private Long id;
    private String word;        //词名
    private String phoneticize; //拼音
    private PriorityStatus priority;

    //多个词义
    private List<MeaningsPoems> meaningsPoemsList;
}
