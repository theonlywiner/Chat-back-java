package chatchatback.pojo.entity;

import chatchatback.pojo.enums.DifficultyStatus;
import chatchatback.pojo.entity.Step;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//'断句技巧表'
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SegmentationTechniques {
    private Long id;
    private String name;
    private String description;
    private DifficultyStatus difficulty;
    private String keywords;    //标志词（逗号分隔）
    private String pitfalls;    //易错点（逗号分隔）

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Step> steps;   //操作步骤
}