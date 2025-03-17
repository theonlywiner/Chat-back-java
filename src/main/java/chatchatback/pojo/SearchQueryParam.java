package chatchatback.pojo;

import lombok.Data;

@Data
public class SearchQueryParam {
    private Integer startIndex;
    private Integer endIndex;
    private String title;
}
