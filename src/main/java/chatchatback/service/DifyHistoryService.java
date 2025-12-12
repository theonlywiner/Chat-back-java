package chatchatback.service;


import chatchatback.pojo.vo.dify.ConversationListVO;
import chatchatback.pojo.vo.dify.ConversationVO;
import chatchatback.pojo.vo.dify.MessageListVO;

public interface DifyHistoryService {
    /**
     * 获取当前用户的会话列表
     */
    ConversationListVO getConversations(String difyUserId, String lastId, Integer limit);

    /**
     * 获取会话的详细消息历史
     */
    MessageListVO getMessages(String conversationId, String difyUserId, String firstId, Integer limit);

    /**
     * 删除会话
     */
    void deleteConversation(String conversationId, String difyUserId);

    /**
     * 重命名会话
     */
    ConversationVO renameConversation(String conversationId, String name, Boolean autoGenerate, String difyUserId);
}