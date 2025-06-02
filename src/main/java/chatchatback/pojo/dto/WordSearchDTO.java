package chatchatback.pojo.dto;

import chatchatback.pojo.enums.PriorityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSearchDTO {
    private String word;
    private String initial;
    private String priority;
    private PriorityStatus status;
}
