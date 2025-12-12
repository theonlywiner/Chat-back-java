package chatchatback.pojo.vo.dify;

import lombok.Data;
import java.util.Map;

/**
 * 会话 VO
 */
@Data
public class ConversationVO {
    private String id;
    private String name;
    //用户输入参数
    private Map<String, Object> inputs;
    //会话状态(normal)
    private String status;
    //开场白
    private String introduction;
    private Long created_at;
    private Long updated_at;
}