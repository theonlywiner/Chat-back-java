package chatchatback.service.impl;

import chatchatback.pojo.vo.dify.ConversationListVO;
import chatchatback.pojo.vo.dify.ConversationVO;
import chatchatback.pojo.vo.dify.MessageListVO;
import chatchatback.properties.DifyProperties;
import chatchatback.service.DifyHistoryService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class DifyHistoryServiceImpl implements DifyHistoryService {

    @Autowired
    private DifyProperties difyProperties;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DifyHistoryServiceImpl() {
        // 重要：配置 ObjectMapper 忽略未知字段
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 获取会话列表
     */
    public ConversationListVO getConversations(String user, String lastId, Integer limit) {
        try {
            String url = buildConversationsUrl(user, lastId, limit);
            log.info("调用Dify获取会话列表: {}", url);

            ResponseEntity<String> response = makeDifyApiCall(url, HttpMethod.GET, null);
            return objectMapper.readValue(response.getBody(), ConversationListVO.class);

        } catch (Exception e) {
            log.error("调用Dify获取会话列表失败: {}", e.getMessage());
            throw new RuntimeException("获取会话列表失败", e);
        }
    }

    /**
     * 获取会话消息
     */
    public MessageListVO getMessages(String conversationId, String user, String firstId, Integer limit) {
        try {
            String url = buildMessagesUrl(conversationId, user, firstId, limit);
            log.info("调用Dify获取会话消息: {}", url);

            ResponseEntity<String> response = makeDifyApiCall(url, HttpMethod.GET, null);
            return objectMapper.readValue(response.getBody(), MessageListVO.class);

        } catch (Exception e) {
            log.error("调用Dify获取会话消息失败: {}", e.getMessage());
            throw new RuntimeException("获取会话消息失败", e);
        }
    }

    /**
     * 删除会话
     */
    public void deleteConversation(String conversationId, String user) {
        try {
            String url = difyProperties.getBaseUrl() + "/conversations/" + conversationId;
            log.info("调用Dify删除会话: {}", url);

            Map<String, String> requestBody = Map.of("user", user);
            makeDifyApiCall(url, HttpMethod.DELETE, requestBody);

        } catch (Exception e) {
            log.error("调用Dify删除会话失败: {}", e.getMessage());
            throw new RuntimeException("删除会话失败", e);
        }
    }

    /**
     * 重命名会话
     */
    public ConversationVO renameConversation(String conversationId, String name, Boolean autoGenerate, String user) {
        try {
            String url = difyProperties.getBaseUrl() + "/conversations/" + conversationId + "/name";
            log.info("调用Dify重命名会话: {}", url);

            Map<String, Object> requestBody = Map.of(
                    "name", name != null ? name : "",
                    "auto_generate", autoGenerate != null ? autoGenerate : false,
                    "user", user
            );

            ResponseEntity<String> response = makeDifyApiCall(url, HttpMethod.POST, requestBody);
            return objectMapper.readValue(response.getBody(), ConversationVO.class);

        } catch (Exception e) {
            log.error("调用Dify重命名会话失败: {}", e.getMessage());
            throw new RuntimeException("重命名会话失败", e);
        }
    }

    /**
     * 构建获取会话列表的URL
     */
    private String buildConversationsUrl(String user, String lastId, Integer limit) {
        String url = difyProperties.getBaseUrl() + "/conversations?user=" + user;
        if (lastId != null && !lastId.trim().isEmpty()) {
            url += "&last_id=" + lastId;
        }
        if (limit != null) {
            url += "&limit=" + limit;
        }
        return url;
    }

    /**
     * 构建获取消息的URL
     */
    private String buildMessagesUrl(String conversationId, String user, String firstId, Integer limit) {
        String url = difyProperties.getBaseUrl() + "/messages?conversation_id=" + conversationId + "&user=" + user;
        if (firstId != null && !firstId.trim().isEmpty()) {
            url += "&first_id=" + firstId;
        }
        if (limit != null) {
            url += "&limit=" + limit;
        }
        return url;
    }

    /**
     * 调用Dify API的通用方法
     */
    private ResponseEntity<String> makeDifyApiCall(String url, HttpMethod method, Object requestBody) {
        try {
            String apiKey = difyProperties.getApiTest();
//            log.info("使用API Key: {}", maskApiKey(apiKey));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity;
            if (requestBody != null) {
                entity = new HttpEntity<>(requestBody, headers);
            } else {
                entity = new HttpEntity<>(headers);
            }

            return restTemplate.exchange(url, method, entity, String.class);

        } catch (Exception e) {
            log.error("Dify API调用失败: {}", e.getMessage());
            throw new RuntimeException("Dify服务调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 隐藏API Key的敏感信息
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}