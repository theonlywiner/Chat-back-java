package chatchatback.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountKeywordVO {
    private Long dynastyId;
    private String dynasty;
    private int count;
}
