package chatchatback.controller;

import chatchatback.properties.DifyProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseChatController {

    private final WebClient webClient;
    private final DifyProperties difyProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储用户连接的 SSE Emitter
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectToChat(HttpServletRequest request) {
        // 从请求属性中获取用户ID（由拦截器设置）
        Integer userId = (Integer) request.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("用户未认证");
        }

        String userKey = "user_" + userId;

        // 创建新的 SSE Emitter
        SseEmitter emitter = new SseEmitter(0L); // 0 表示不超时

        // 存储 emitter
        userEmitters.put(userKey, emitter);

        // 设置完成和超时回调
        emitter.onCompletion(() -> {
            log.info("SSE 连接完成: {}", userKey);
            userEmitters.remove(userKey);
        });

        emitter.onTimeout(() -> {
            log.info("SSE 连接超时: {}", userKey);
            userEmitters.remove(userKey);
        });

        emitter.onError((e) -> {
            log.error("SSE 连接错误: {}", userKey, e);
            userEmitters.remove(userKey);
        });

        // 发送连接成功消息
        try {
            sendSseEvent(emitter, "connected", Map.of(
                    "message", "连接成功，准备调用 Dify 大模型",
                    "userId", userId,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("发送连接成功消息失败: {}", e.getMessage());
        }

        log.info("SSE 连接建立: {}", userKey);
        return emitter;
    }

    /**
     * 发送消息到 Dify
     */
    @PostMapping("/chat/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> message,
                                         HttpServletRequest request) {
        // 从请求属性中获取用户ID（由拦截器设置）
        Integer userId = (Integer) request.getAttribute("userId");

        log.info("发送消息 - 从请求属性获取的用户ID: {}", userId);
        log.info("收到的消息体: {}", message);

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户未认证"));
        }

        String userKey = "user_" + userId;

        SseEmitter emitter = userEmitters.get(userKey);
        if (emitter == null) {
            log.error("未找到用户 {} 的活跃连接", userKey);
            return ResponseEntity.badRequest().body(Map.of("error", "未找到活跃的连接"));
        }

        // 在后台处理 Dify 调用
        executorService.submit(() -> {
            try {
                handleDifyChatMessage(emitter, message, userKey);
            } catch (Exception e) {
                log.error("处理 Dify 消息失败: {}", e.getMessage(), e);
                try {
                    sendSseEvent(emitter, "error", Map.of(
                            "message", "处理消息失败: " + e.getMessage()
                    ));
                } catch (Exception ex) {
                    log.error("发送错误消息失败: {}", ex.getMessage());
                }
            }
        });

        return ResponseEntity.ok(Map.of("status", "消息已发送"));
    }

    /**
     * 处理 Dify 聊天消息
     */
    private void handleDifyChatMessage(SseEmitter emitter, Map<String, Object> request, String userKey) {
//        log.info("开始处理 Dify 聊天消息，请求内容: {}", request);

        // 检查 content 字段
        if (!request.containsKey("content")) {
            log.error("请求中缺少 content 字段");
            sendSseEvent(emitter, "error", Map.of("message", "消息内容不能为空"));
            return;
        }

        Object contentObj = request.get("content");
        if (contentObj == null) {
            log.error("content 字段为 null");
            sendSseEvent(emitter, "error", Map.of("message", "消息内容不能为空"));
            return;
        }

        String userQuery = contentObj.toString();
        log.info("提取的用户查询: {}", userQuery);

        if (userQuery.trim().isEmpty()) {
            log.error("用户查询内容为空");
            sendSseEvent(emitter, "error", Map.of("message", "消息内容不能为空"));
            return;
        }

        // 检查 conversation_id 字段
        String conversationId = "";
        if (request.containsKey("conversation_id")) {
            Object conversationIdObj = request.get("conversation_id");
            if (conversationIdObj != null) {
                conversationId = conversationIdObj.toString();
                log.info("提取的会话ID: {}", conversationId);
            }
        }

        log.info("调用 Dify 流式 API - 用户: {}, 查询: {}", userKey, userQuery);

        // 发送开始消息
        try {
            sendSseEvent(emitter, "chat_start", Map.of(
                    "message", "开始调用 Dify 大模型生成回复...",
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("发送开始消息失败: {}", e.getMessage());
        }

        // 创建 Dify 请求体
        Map<String, Object> requestBody = createDifyRequestBody(userQuery, userKey, conversationId);

        // 调用 Dify API
        callDifyStreamingAPI(emitter, requestBody);
    }

    /**
     * 创建 Dify 请求体
     */
    private Map<String, Object> createDifyRequestBody(String query, String user, String conversationId) {
        return Map.of(
                "inputs", Map.of(),
                "query", query,
                "response_mode", "streaming",
                "user", user,
                "conversation_id", conversationId,
                "auto_generate_name", true
        );
    }

    /**
     * 调用 Dify 流式 API
     */
    private void callDifyStreamingAPI(SseEmitter emitter, Map<String, Object> requestBody) {
        try {
            String apiUrl = difyProperties.getBaseUrl() + "/chat-messages";
            String apiKey = difyProperties.getApiTest();

            log.info("调用 Dify API: {}", apiUrl);
            log.info("Dify 请求体: {}", requestBody);

            Flux<String> responseFlux = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class);

            // 处理流式响应
            responseFlux.subscribe(
                    chunk -> {
//                        log.info("收到 Dify 原始响应块: {}", chunk);
                        processDifyChunk(emitter, chunk);
                    },
                    error -> {
//                        log.error("Dify 流式调用错误: {}", error.getMessage(), error);
                        try {
                            sendSseEvent(emitter, "error", Map.of(
                                    "message", "大模型服务调用失败: " + error.getMessage()
                            ));
                        } catch (Exception e) {
                            log.error("发送错误消息失败: {}", e.getMessage());
                        }
                    },
                    () -> {
//                        log.info("Dify 流式输出完成");
                        try {
                            sendSseEvent(emitter, "chat_complete", Map.of(
                                    "message", "流式输出完成",
                                    "timestamp", System.currentTimeMillis()
                            ));
                        } catch (Exception e) {
                            log.error("发送完成消息失败: {}", e.getMessage());
                        }
                    }
            );

        } catch (Exception e) {
            log.error("调用 Dify API 失败: {}", e.getMessage(), e);
            try {
                sendSseEvent(emitter, "error", Map.of(
                        "message", "调用大模型服务失败: " + e.getMessage()
                ));
            } catch (Exception ex) {
                log.error("发送错误消息失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 处理 Dify 数据块
     */
    private void processDifyChunk(SseEmitter emitter, String chunk) {
        try {
            if (chunk == null || chunk.trim().isEmpty()) {
                return;
            }

            String jsonStr = chunk.trim();
//            log.info("处理 Dify 数据块: {}", jsonStr);

            if ("[DONE]".equals(jsonStr)) {
                log.info("收到 Dify 流式结束标记");
                return;
            }

            if (!jsonStr.isEmpty()) {
                JsonNode eventData = objectMapper.readTree(jsonStr);
                processDifyEvent(emitter, eventData);
            }
        } catch (Exception e) {
            log.error("处理 Dify 数据块失败: {}, 原始数据: {}", e.getMessage(), chunk, e);
        }
    }

    /**
     * 处理 Dify 事件
     */
    private void processDifyEvent(SseEmitter emitter, JsonNode eventData) {
        try {
            String eventType = eventData.has("event") ?
                    eventData.get("event").asText() : "unknown";

//            log.info("处理 Dify 事件 - 类型: {}, 完整数据: {}", eventType, eventData);

            switch (eventType) {
                case "message":
//                    log.info("开始处理 message 事件");
                    handleDifyMessageEvent(emitter, eventData);
                    break;
                case "message_end":
//                    log.info("开始处理 message_end 事件");
                    handleDifyMessageEndEvent(emitter, eventData);
                    break;
                case "workflow_started":
                case "node_started":
                case "node_finished":
                case "workflow_finished":
//                    log.info("开始处理工作流事件: {}", eventType);
                    handleDifyWorkflowEvent(emitter, eventData);
                    break;
                case "error":
//                    log.info("开始处理错误事件");
                    handleDifyErrorEvent(emitter, eventData);
                    break;
                case "ping":
                    log.info("忽略 ping 事件");
                    break;
                default:
                    log.warn("未知的 Dify 事件类型: {}, 数据: {}", eventType, eventData);
                    break;
            }
        } catch (Exception e) {
            log.error("处理 Dify 事件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理 Dify 消息事件
     */
    private void handleDifyMessageEvent(SseEmitter emitter, JsonNode eventData) {
        try {
            if (eventData.has("answer")) {
                String answerChunk = eventData.get("answer").asText();
//                log.info("处理 Dify message 事件，answer: {}", answerChunk);

                Map<String, Object> response = new java.util.HashMap<>();
                response.put("type", "chat_chunk");
                response.put("content", answerChunk);
                response.put("event", "message");
                response.put("timestamp", System.currentTimeMillis());

                if (eventData.has("message_id") && !eventData.get("message_id").isNull()) {
                    response.put("message_id", eventData.get("message_id").asText());
                }
                if (eventData.has("conversation_id") && !eventData.get("conversation_id").isNull()) {
                    response.put("conversation_id", eventData.get("conversation_id").asText());
                }
                if (eventData.has("task_id") && !eventData.get("task_id").isNull()) {
                    response.put("task_id", eventData.get("task_id").asText());
                }

                sendSseEvent(emitter, "chat_chunk", response);
//                log.info("已发送 chat_chunk 事件到前端");
            } else {
                log.warn("Dify message 事件中没有 answer 字段");
            }
        } catch (Exception e) {
            log.error("处理 Dify 消息事件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理 Dify 消息结束事件
     */
    private void handleDifyMessageEndEvent(SseEmitter emitter, JsonNode eventData) {
        try {
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("type", "chat_complete");
            response.put("event", "message_end");
            response.put("timestamp", System.currentTimeMillis());

            if (eventData.has("conversation_id") && !eventData.get("conversation_id").isNull()) {
                response.put("conversation_id", eventData.get("conversation_id").asText());
            }

            sendSseEvent(emitter, "chat_complete", response);
            log.info("Dify 流式输出完成");

        } catch (Exception e) {
            log.error("处理 Dify 消息结束事件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理 Dify 工作流事件
     */
    private void handleDifyWorkflowEvent(SseEmitter emitter, JsonNode eventData) {
        try {
            // 过滤掉 node_started 事件
            if ("node_started".equals(eventData.get("event").asText())) {
                log.info("过滤掉 node_started 事件");
                return;
            }

            Map<String, Object> response = Map.of(
                    "type", "workflow_event",
                    "event", eventData.get("event").asText(),
                    "data", eventData.toString(),
                    "timestamp", System.currentTimeMillis()
            );

            sendSseEvent(emitter, "workflow_event", response);
//            log.info("已发送工作流事件: {}", eventData.get("event").asText());

        } catch (Exception e) {
            log.error("处理 Dify 工作流事件失败: {}", e.getMessage());
        }
    }

    /**
     * 处理 Dify 错误事件
     */
    private void handleDifyErrorEvent(SseEmitter emitter, JsonNode eventData) {
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

            sendSseEvent(emitter, "error", response);
            log.error("Dify 返回错误: {}", errorMessage);

        } catch (Exception e) {
            log.error("处理 Dify 错误事件失败: {}", e.getMessage());
        }
    }

    /**
     * 发送 SSE 事件
     */
    private void sendSseEvent(SseEmitter emitter, String eventType, Object data) {
        try {
            Map<String, Object> event = Map.of(
                    "type", eventType,
                    "data", data
            );

            String eventJson = objectMapper.writeValueAsString(event);
//            log.info("发送 SSE 事件 - 类型: {}, 数据: {}", eventType, eventJson);

            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .data(eventJson)
                    .name("message");

            emitter.send(eventBuilder);
//            log.info("SSE 事件发送成功: {}", eventType);
        } catch (Exception e) {
            log.error("发送 SSE 事件失败: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }
}