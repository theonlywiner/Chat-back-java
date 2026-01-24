package chatchatback.service.impl;

import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.entity.Poem;
import chatchatback.properties.CozeProperties;
import chatchatback.service.CozeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class CozeServiceImpl implements CozeService {

    private final CozeProperties cozeProperties;
    private final PoemMapper poemMapper;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public JsonNode evaluateRecitation(MultipartFile file, Long poemId) throws Exception {
        // 1. 从数据库获取标准古诗文文本
        Poem poem = poemMapper.getPoemById(poemId);
        if (poem == null) {
            throw new RuntimeException("古诗文不存在，ID: " + poemId);
        }
        String standardText = poem.getName() + "\n" + poem.getFullAncientContent();
        String poemName = poem.getName();
        log.info("从数据库获取古诗文，ID: {}, 标题: {}, 内容: {}", poemId, poemName, standardText);

        log.info("开始调用 Coze 工作流，音频文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // 2. 上传文件到 Coze，获取文件 ID
        String fileId = uploadFileToCoze(file);
        log.info("文件上传成功，file_id: {}", fileId);
        
        // 3. 构建 Coze 工作流请求参数（JSON 格式）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("workflow_id", cozeProperties.getWorkflowId());
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("standard_text", standardText);
        
        // Coze File 类型参数可能需要对象格式，尝试不同的格式
        Map<String, String> fileObject = new HashMap<>();
        fileObject.put("type", "file");
        fileObject.put("file_id", fileId);
        parameters.put("student_video", fileObject);
        
        requestBody.put("parameters", parameters);

        log.info("调用 Coze 工作流，workflow_id: {}, 古诗文: {}", 
                cozeProperties.getWorkflowId(), poemName);
        log.info("Coze API URL: {}", cozeProperties.getApiUrl());
        log.info("Coze Token (前10字符): {}...", cozeProperties.getToken().substring(0, Math.min(10, cozeProperties.getToken().length())));
        log.info("请求参数: {}", objectMapper.writeValueAsString(requestBody));

        // 4. 调用 Coze SSE 接口
        AtomicReference<StringBuilder> sseDataBuilder = new AtomicReference<>(new StringBuilder());
        AtomicReference<Exception> errorRef = new AtomicReference<>();
        
        try {
            Flux<String> responseFlux = webClient.post()
                    .uri(cozeProperties.getApiUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + cozeProperties.getToken())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Coze API 客户端错误: {}, 响应: {}", response.statusCode(), errorBody);
                                    return reactor.core.publisher.Mono.error(new RuntimeException(
                                            "Coze API 认证失败: " + response.statusCode() + " - " + errorBody));
                                });
                    })
                    .onStatus(status -> status.is5xxServerError(), response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Coze API 服务器错误: {}, 响应: {}", response.statusCode(), errorBody);
                                    return reactor.core.publisher.Mono.error(new RuntimeException(
                                            "Coze API 服务器错误: " + response.statusCode() + " - " + errorBody));
                                });
                    })
                    .bodyToFlux(String.class);

            // 5. 处理 SSE 流式响应
            responseFlux.collectList().block().forEach(chunk -> {
                try {
                    log.info("收到 Coze SSE 数据块: {}", chunk);
                    sseDataBuilder.get().append(chunk).append("\n");
                } catch (Exception e) {
                    log.error("处理 SSE 数据块失败: {}", e.getMessage());
                    errorRef.set(e);
                }
            });

            if (errorRef.get() != null) {
                throw errorRef.get();
            }

            // 6. 解析 SSE 响应，提取最终的 JSON 数据
            String sseData = sseDataBuilder.get().toString();
            log.info("完整 SSE 响应: {}", sseData);
            log.info("SSE 响应长度: {}, 前500字符: {}", sseData.length(), 
                    sseData.substring(0, Math.min(500, sseData.length())));
            
            JsonNode result = parseSseResponse(sseData);
            log.info("解析后的 Coze 评估结果: {}", result);
            
            return result;

        } catch (Exception e) {
            log.error("调用 Coze 工作流失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用 Coze 工作流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件到 Coze 平台，返回文件 ID
     */
    private String uploadFileToCoze(MultipartFile file) throws Exception {
        log.info("开始上传文件到 Coze: {}", file.getOriginalFilename());
        
        // 构建 multipart 请求体
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        }).contentType(MediaType.parseMediaType(file.getContentType() != null ? file.getContentType() : "audio/mpeg"));

        // 调用 Coze 文件上传 API
        String uploadUrl = "https://api.coze.cn/v1/files/upload";
        
        try {
            Mono<JsonNode> responseMono = webClient.post()
                    .uri(uploadUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + cozeProperties.getToken())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Coze 文件上传失败: {}, 响应: {}", response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException(
                                            "Coze 文件上传失败: " + response.statusCode() + " - " + errorBody));
                                });
                    })
                    .bodyToMono(String.class)
                    .map(responseBody -> {
                        try {
                            return objectMapper.readTree(responseBody);
                        } catch (Exception e) {
                            throw new RuntimeException("解析上传响应失败: " + e.getMessage(), e);
                        }
                    });

            JsonNode uploadResponse = responseMono.block();
            log.info("Coze 文件上传响应: {}", uploadResponse);
            
            // 从响应中提取文件 ID（直接返回 ID，不构造 URL）
            if (uploadResponse.has("data") && uploadResponse.get("data").has("id")) {
                String fileId = uploadResponse.get("data").get("id").asText();
                log.info("Coze 文件 ID: {}", fileId);
                return fileId;  // 直接返回 file_id
            } else {
                throw new RuntimeException("文件上传成功，但未返回文件 ID: " + uploadResponse.toString());
            }
            
        } catch (Exception e) {
            log.error("上传文件到 Coze 失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件到 Coze 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 SSE 响应数据，提取最终的 JSON 结果
     * Coze 工作流返回的 SSE 数据格式：
     * event: Message
     * data: {"event":"Message","message":"xxx"}
     * 
     * event: Done
     * data: {"event":"Done","data":{"output":"...JSON结果..."}}
     */
    private JsonNode parseSseResponse(String sseData) throws Exception {
        log.info("开始解析 SSE 响应，原始数据长度: {}", sseData.length());
        
        String[] lines = sseData.split("\\n");
        log.info("总共分割为 {} 行", lines.length);
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // 跳过空行
            if (line.isEmpty()) {
                continue;
            }
            
            log.info("处理第 {} 行: {}", i, line);
            
            try {
                // 直接解析每一行为 JSON（不是 data: 格式）
                JsonNode eventData = objectMapper.readTree(line);
                log.info("解析的 JSON 节点: {}", eventData.toString());
                
                // 检查是否包含 content 字段
                if (eventData.has("content")) {
                    String content = eventData.get("content").asText();
                    log.info("找到 content 字段: {}", content);
                    
                    // 检查 content 中是否包含 parsed_json
                    if (content.contains("parsed_json")) {
                        log.info("找到包含 parsed_json 的内容");
                        
                        // 提取 parsed_json 字段
                        try {
                            // content 是一个 JSON 字符串，需要解析
                            log.info("尝试解析 content 为 JSON");
                            JsonNode contentJson = objectMapper.readTree(content);
                            log.info("解析成功，contentJson: {}", contentJson.toString());
                            
                            if (contentJson.has("parsed_json")) {
                                JsonNode parsedJson = contentJson.get("parsed_json");
                                
                                // 检查 parsed_json 是否为 null
                                if (parsedJson.isNull()) {
                                    log.warn("parsed_json 值为 null，工作流可能执行失败");
                                    continue;
                                }
                                
                                log.info("成功提取 parsed_json: {}", parsedJson.toString());
                                return parsedJson;
                            } else {
                                log.warn("contentJson 中没有 parsed_json 字段");
                            }
                        } catch (Exception e) {
                            log.error("解析 content 失败: {}, content 内容: {}", e.getMessage(), content, e);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("解析 SSE 数据行失败: {}", e.getMessage());
            }
        }
        
        // 如果没找到，返回错误信息
        log.warn("未找到有效的 parsed_json 字段（可能为 null 或不存在）");
        return objectMapper.createObjectNode().put("error", "工作流执行失败或未返回评估结果");
    }
}
