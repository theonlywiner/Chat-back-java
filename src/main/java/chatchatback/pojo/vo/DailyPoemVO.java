package chatchatback.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class DailyPoemVO {
    private String title;
    private String author;
    private List<String> content;
    private String dynasty;
}
