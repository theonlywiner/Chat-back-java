package chatchatback.pojo.dto;

import lombok.Data;

@Data
public class SearchQueryParamDTO {
    private Integer startIndex;
    private Integer endIndex;
    private String title;
}
