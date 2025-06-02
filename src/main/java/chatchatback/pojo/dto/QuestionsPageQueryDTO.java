package chatchatback.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsPageQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 5;
    private String word;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
