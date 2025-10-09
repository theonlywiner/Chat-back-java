package chatchatback.pojo.dto;

import chatchatback.pojo.enums.DifficultyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SegmentationTechniquesDTO {
    private Long id;
    private String name;
    private String description;
    private DifficultyStatus difficulty;
}
