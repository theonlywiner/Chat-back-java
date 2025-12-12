package chatchatback.pojo.vo.dify;

import lombok.Data;
import java.util.List;

/**
 * 消息列表 VO
 */
@Data
public class MessageListVO {
    //返回消息数量
    private Integer limit;
    //是否还有更多
    private Boolean has_more;
    //消息列表
    private List<MessageVO> data;
}