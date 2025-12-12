package chatchatback.pojo.vo.dify;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 消息 VO
 */
@Data
public class MessageVO {
    private String id;
    // 会话ID
    private String conversation_id;
    //用户输入参数
    private Map<String, Object> inputs;
    //用户提问内容
    private String query;
    //ai回答内容
    private String answer;
    //消息关联文件列表
    private List<FileInfoVO> message_files;
    //用户反馈
    private Object feedback;
    //引用和归属分段列表
    private List<ResourceVO> retriever_resources;
    private Long created_at;
}