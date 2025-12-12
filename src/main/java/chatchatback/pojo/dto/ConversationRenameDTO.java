package chatchatback.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 重命名会话 DTO
 */
@Data
public class ConversationRenameDTO {

    @NotNull(message = "会话名称不能为空")
    private String name;

    private Boolean auto_generate = false;
}