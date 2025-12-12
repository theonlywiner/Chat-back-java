package chatchatback.handler;

import chatchatback.constant.Constant;
import chatchatback.mapper.GradeMapper;
import chatchatback.properties.DifyProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DifyChatWebSocketHandler extends TextWebSocketHandler {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private DifyProperties difyProperties;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public DifyChatWebSocketHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        Integer userId = (Integer) session.getAttributes().get("userId");

        if (userId == null) {
            log.error("WebSocket 连接建立但未找到用户ID，关闭连接");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("未认证"));
            return;
        }

        sessions.put(sessionId, session);
        log.info("Dify WebSocket 连接建立: {}, 用户ID: {}", sessionId, userId);

        Map<String, Object> response = Map.of(
                "type", "connected",
                "message", "连接成功，准备调用 Dify 大模型",
                "sessionId", sessionId,
                "userId", userId,
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(session, response);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("收到前端消息: {}", payload);

        try {
            JsonNode request = objectMapper.readTree(payload);
            String messageType = request.has("type") ? request.get("type").asText() : "unknown";

            switch (messageType) {
                case "chat":
                    handleDifyChatMessage(session, request);
                    break;
                case "ping":
                    handlePingMessage(session);
                    break;
                default:
                    handleUnknownMessage(session, messageType);
                    break;
            }

        } catch (Exception e) {
            log.error("消息处理失败: {}", e.getMessage());
            sendError(session, "消息格式错误: " + e.getMessage());
        }
    }

    /**
     * 处理 Dify 聊天消息
     */
    private void handleDifyChatMessage(WebSocketSession session, JsonNode request) {
        if (!request.has("content")) {
            sendError(session, "消息内容不能为空");
            return;
        }

        String userQuery = request.get("content").asText();
        if (userQuery.trim().isEmpty()) {
            sendError(session, "消息内容不能为空");
            return;
        }

        Integer currentId = (Integer) session.getAttributes().get("userId");
        if (currentId == null) {
            log.error("WebSocket session 中未找到用户ID");
            sendError(session, "用户未认证");
            return;
        }

        String userId = "user_" + currentId;
        String conversationId = request.has("conversation_id") && !request.get("conversation_id").isNull()
                ? request.get("conversation_id").asText() : "";
        Integer gradeId = gradeMapper.getGradeIdById(currentId);
        String grade = gradeMapper.getGradeNameByGradeId(gradeId);

        log.info("收到用户conversation_id: {}", conversationId);
        log.info("调用 Dify 流式 API - 用户: {}, 查询: {}, 年级id:{} ", userId, userQuery, gradeId);
        log.info("年级: {}", grade);
        sendStartMessage(session);

        // 根据 Dify 文档修正请求体格式
        Map<String, Object> requestBody = createDifyRequestBody(userQuery, gradeId, grade, userId, conversationId);
        log.info("请求体: {}", requestBody);
        callDifyStreamingAPI(session, requestBody);
    }

    /**
     * 创建符合 Dify API 规范的请求体
     */
    private Map<String, Object> createDifyRequestBody(String query, Integer gradeId,String grade, String user, String conversationId) {
        // 创建包含grade的inputs
        Map<String, Object> inputs = new HashMap<>();
        if (grade != null && !grade.trim().isEmpty()) {
            inputs.put("grade", grade);
        }else {
            // 如果没有年级名称，使用默认值或从 gradeId 推导
            inputs.put("grade", "全年级");
        }
        if (gradeId > 0 && gradeId <= Constant.MaxGradeId) {
            inputs.put("gradeId", gradeId);
        }else {
            inputs.put("gradeId", Constant.MaxGradeId);
        }
        return Map.of(
                "inputs", inputs, // 根据应用需要的变量填写
                "query", query,
                "response_mode", "streaming",
                "user", user,
                "conversation_id", conversationId,
                "auto_generate_name", true
        );
    }

    /**
     * 使用 WebClient 调用 Dify 流式 API
     */
    private void callDifyStreamingAPI(WebSocketSession session, Map<String, Object> requestBody) {
        try {
            String apiUrl = difyProperties.getBaseUrl() + "/chat-messages";
            String apiKey = difyProperties.getApiTest();

            log.info("调用 Dify API: {}", apiUrl);
            log.info("使用 API Key: {}", maskApiKey(apiKey));

            Flux<String> responseFlux = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Dify API 客户端错误: {}, 响应: {}", response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Dify API 客户端错误: " + response.statusCode() + " - " + errorBody));
                                });
                    })
                    .onStatus(status -> status.is5xxServerError(), response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Dify API 服务器错误: {}, 响应: {}", response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Dify API 服务器错误: " + response.statusCode() + " - " + errorBody));
                                });
                    })
                    .bodyToFlux(String.class);

            // 订阅流式响应
            responseFlux.subscribe(
                    chunk -> {
                        log.debug("收到 Dify 数据块: {}", chunk);
                        processDifyChunk(session, chunk);
                    },
                    error -> {
                        log.error("Dify 流式调用错误: {}", error.getMessage());
                        sendError(session, "大模型服务调用失败: " + error.getMessage());
                    },
                    () -> {
                        log.info("Dify 流式输出完成");
                        sendCompleteMessage(session);
                    }
            );

        } catch (Exception e) {
            log.error("调用 Dify API 失败: {}", e.getMessage());
            sendError(session, "调用大模型服务失败: " + e.getMessage());
        }
    }

    /**
     * 处理 Dify 返回的数据块 - 修正：直接处理 JSON 格式
     */
    private void processDifyChunk(WebSocketSession session, String chunk) {
        try {
            if (chunk == null || chunk.trim().isEmpty()) {
                return;
            }

            // 直接解析 JSON，不是 SSE 格式
            String jsonStr = chunk.trim();

            // 检查是否是结束标记
            if ("[DONE]".equals(jsonStr)) {
                log.info("收到 Dify 流式结束标记");
                return;
            }

            if (!jsonStr.isEmpty()) {
                JsonNode eventData = objectMapper.readTree(jsonStr);
                processDifyEvent(session, eventData);
            }
        } catch (Exception e) {
            log.error("处理 Dify 数据块失败: {}, 原始数据: {}", e.getMessage(), chunk);
        }
    }

    /**
     * 处理 Dify 事件
     */
    private void processDifyEvent(WebSocketSession session, JsonNode eventData) {
        try {
            String eventType = eventData.has("event") ?
                    eventData.get("event").asText() : "unknown";

            log.debug("处理 Dify 事件: {}", eventType);

            switch (eventType) {
                case "message":
                    handleDifyMessageEvent(session, eventData);
                    break;
                case "message_end":
                    handleDifyMessageEndEvent(session, eventData);
                    break;
                case "workflow_started":
                case "node_started":
                case "node_finished":
                case "workflow_finished":
                    handleDifyWorkflowEvent(session, eventData);
                    break;
                case "error":
                    handleDifyErrorEvent(session, eventData);
                    break;
                case "ping":
                    // 忽略 ping 事件
                    break;
                default:
                    log.debug("未知的 Dify 事件类型: {}", eventType);
                    break;
            }
        } catch (Exception e) {
            log.error("处理 Dify 事件失败: {}", e.getMessage());
        }
    }

    /**
     * 处理 Dify 消息事件（文本块） - 修正：直接转发给前端
     */
    private void handleDifyMessageEvent(WebSocketSession session, JsonNode eventData) {
        try {
            if (eventData.has("answer")) {
                String answerChunk = eventData.get("answer").asText();

                // 使用 HashMap 而不是 Map.of()，避免 null 值问题
                Map<String, Object> response = new HashMap<>();
                response.put("type", "chat_chunk");
                response.put("content", answerChunk);
                response.put("event", "message");
                response.put("timestamp", System.currentTimeMillis());

                // 只添加存在的字段
                if (eventData.has("message_id") && !eventData.get("message_id").isNull()) {
                    response.put("message_id", eventData.get("message_id").asText());
                }
                if (eventData.has("conversation_id") && !eventData.get("conversation_id").isNull()) {
                    response.put("conversation_id", eventData.get("conversation_id").asText());
                }
                if (eventData.has("task_id") && !eventData.get("task_id").isNull()) {
                    response.put("task_id", eventData.get("task_id").asText());
                }

                sendMessage(session, response);
                log.debug("发送文本块到前端: {}", answerChunk);
            }
        } catch (Exception e) {
            log.error("处理 Dify 消息事件失败: {}", e.getMessage(), e); // 添加完整堆栈跟踪
        }
    }
    /**
     * 处理 Dify 消息结束事件
     */
    private void handleDifyMessageEndEvent(WebSocketSession session, JsonNode eventData) {
        try {
            // 使用 HashMap 而不是 Map.of()
            Map<String, Object> response = new HashMap<>();
            response.put("type", "chat_complete");
            response.put("event", "message_end");
            response.put("timestamp", System.currentTimeMillis());

            // 只添加存在的字段
            if (eventData.has("metadata") && !eventData.get("metadata").isNull()) {
                response.put("metadata", eventData.get("metadata"));
            }
            if (eventData.has("conversation_id") && !eventData.get("conversation_id").isNull()) {
                response.put("conversation_id", eventData.get("conversation_id").asText());
            }
            if (eventData.has("message_id") && !eventData.get("message_id").isNull()) {
                response.put("message_id", eventData.get("message_id").asText());
            }
            if (eventData.has("task_id") && !eventData.get("task_id").isNull()) {
                response.put("task_id", eventData.get("task_id").asText());
            }

            sendMessage(session, response);
            log.info("Dify 流式输出完成");

        } catch (Exception e) {
            log.error("处理 Dify 消息结束事件失败: {}", e.getMessage(), e); // 添加完整堆栈跟踪
        }
    }
    /**
     * 处理 Dify 工作流事件
     */
    private void handleDifyWorkflowEvent(WebSocketSession session, JsonNode eventData) {
        try {
            Map<String, Object> response = Map.of(
                    "type", "workflow_event",
                    "event", eventData.get("event").asText(),
                    "data", eventData,
                    "timestamp", System.currentTimeMillis()
            );

            sendMessage(session, response);
            log.debug("发送工作流事件: {}", eventData.get("event").asText());

        } catch (Exception e) {
            log.error("处理 Dify 工作流事件失败: {}", e.getMessage());
        }
    }

    /**
     * 处理 Dify 错误事件
     */
    private void handleDifyErrorEvent(WebSocketSession session, JsonNode eventData) {
        try {
            String errorMessage = "Dify 服务错误";
            if (eventData.has("message")) {
                errorMessage = eventData.get("message").asText();
            }

            Map<String, Object> response = Map.of(
                    "type", "error",
                    "event", "error",
                    "message", errorMessage,
                    "timestamp", System.currentTimeMillis()
            );

            // 添加错误详情
            if (eventData.has("code")) {
                response.put("code", eventData.get("code").asText());
            }
            if (eventData.has("status")) {
                response.put("status", eventData.get("status").asInt());
            }

            sendMessage(session, response);
            log.error("Dify 返回错误: {}", errorMessage);

        } catch (Exception e) {
            log.error("处理 Dify 错误事件失败: {}", e.getMessage());
        }
    }

    /**
     * 发送开始处理消息
     */
    private void sendStartMessage(WebSocketSession session) {
        try {
            Map<String, Object> response = Map.of(
                    "type", "chat_start",
                    "message", "开始调用 Dify 大模型生成回复...",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(session, response);
        } catch (IOException e) {
            log.error("发送开始消息失败: {}", e.getMessage());
        }
    }

    /**
     * 发送完成消息
     */
    private void sendCompleteMessage(WebSocketSession session) {
        try {
            Map<String, Object> response = Map.of(
                    "type", "complete",
                    "message", "流式输出完成",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(session, response);
        } catch (IOException e) {
            log.error("发送完成消息失败: {}", e.getMessage());
        }
    }

    /**
     * 处理心跳消息
     */
    private void handlePingMessage(WebSocketSession session) throws IOException {
        Map<String, Object> response = Map.of(
                "type", "pong",
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(session, response);
    }

    /**
     * 处理未知消息类型
     */
    private void handleUnknownMessage(WebSocketSession session, String messageType) throws IOException {
        Map<String, Object> response = Map.of(
                "type", "error",
                "message", "未知的消息类型: " + messageType
        );
        sendMessage(session, response);
    }

    /**
     * 发送错误消息
     */
    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            Map<String, Object> response = Map.of(
                    "type", "error",
                    "message", errorMessage,
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(session, response);
        } catch (IOException e) {
            log.error("发送错误消息失败: {}", e.getMessage());
        }
    }

    /**
     * 发送消息到客户端
     */
    private void sendMessage(WebSocketSession session, Map<String, Object> message) throws IOException {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            log.error("发送消息失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 隐藏 API Key 的敏感信息（用于日志）
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);

        Integer userId = (Integer) session.getAttributes().get("userId");
        log.info("WebSocket 连接关闭: {}, 用户ID: {}, 状态: {}", sessionId, userId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Integer userId = (Integer) session.getAttributes().get("userId");
        log.error("WebSocket 传输错误 - 用户ID: {}, 错误: {}", userId, exception.getMessage());
        sessions.remove(session.getId());
    }
}