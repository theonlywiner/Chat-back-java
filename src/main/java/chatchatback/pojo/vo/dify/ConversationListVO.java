package chatchatback.pojo.vo.dify;

import lombok.Data;
import java.util.List;

/**
 * 会话列表 VO
 */
@Data
public class ConversationListVO {
    //返回记录数量
    private Integer limit;
    //是否有更多数据
    private Boolean has_more;
    //会话列表数组
    private List<ConversationVO> data;
}