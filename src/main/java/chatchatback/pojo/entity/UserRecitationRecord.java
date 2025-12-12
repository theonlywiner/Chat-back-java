package chatchatback.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "user_recitation_records", autoResultMap = true)
public class UserRecitationRecord {
    private Long id;
    private Long userId;
    private Long poemId;
    @TableField(exist = false)
    private String poemName;

    private String studentText;

    private Integer overallScore;
    private Integer contentCompleteness;
    private Integer structuralCorrectness;
    private Integer keyImageryPreservation;

    private String praiseFeedback;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<MajorError> majorErrors;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> suggestions;

    private LocalDateTime createdAt;
}
