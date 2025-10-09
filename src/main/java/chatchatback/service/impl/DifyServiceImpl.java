package chatchatback.service.impl;

import chatchatback.properties.DifyProperties;
import chatchatback.service.DifyService;
import chatchatback.utils.CurrentHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DifyServiceImpl implements DifyService {

    private final DifyProperties difyProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public JsonNode uploadFile(MultipartFile file) throws Exception {
        Integer currentId = CurrentHolder.getCurrentId();
        if (currentId == null) {
            log.error("CurrentHolder.getCurrentId() returned null - token may be missing or interceptor not applied");
            throw new RuntimeException("user not authenticated");
        }
        String userId = "Dify" + currentId;
        log.info("userId: {}", userId);
        String url = difyProperties.getBaseUrl() + "/files/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(difyProperties.getApiKey());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new chatchatback.utils.MultipartInputStreamFileResource(
                file.getInputStream(), file.getOriginalFilename(), file.getSize()));
        body.add("user", userId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        log.info("Uploading file to Dify: {}", file.getOriginalFilename());
        log.info("Using API Key: {}...", difyProperties.getApiKey().substring(0, Math.min(difyProperties.getApiKey().length(), 10)));

        ResponseEntity<String> resp = restTemplate.postForEntity(url, requestEntity, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            log.error("Upload failed with status: {}, body: {}", resp.getStatusCode(), resp.getBody());
            throw new RuntimeException("upload file failed: " + resp.getStatusCode().value() + " body=" + resp.getBody());
        }

        JsonNode response = objectMapper.readTree(resp.getBody());
        log.info("File uploaded successfully, file ID: {}", response.get("id"));
        return response;
    }

    @Override
    public JsonNode runWorkflow(String standardText, String uploadFileId) throws Exception {
        String url = difyProperties.getBaseUrl() + "/chat-messages";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(difyProperties.getApiKey());

        // ensure current user id
        Integer currentId = CurrentHolder.getCurrentId();
        if (currentId == null) {
            log.error("CurrentHolder.getCurrentId() returned null - token may be missing or interceptor not applied");
            throw new RuntimeException("user not authenticated");
        }
        String userId = "Dify" + currentId;

        // 构建正确的请求体 - 包含 student_video 参数
        Map<String, Object> requestBody = Map.of(
                "inputs", Map.of(
                        "standard_text", standardText,
                        "student_video", Map.of(  // 添加 student_video 参数
                                "type", "video",
                                "transfer_method", "local_file",
                                "upload_file_id", uploadFileId
                        )
                ),
                "query", "请对学生的古诗文背诵进行评分",  // 添加明确的查询内容
                "response_mode", "blocking",
                "user", userId
                // 注意：移除了外层的 files 数组，因为现在 student_video 在 inputs 中
        );

        String body = objectMapper.writeValueAsString(requestBody);

        log.info("Sending chat message with file ID: {}", uploadFileId);
        log.info("Request body: {}", body);

        HttpEntity<String> req = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, req, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            log.error("Chat message failed - Status: {}, Response: {}", resp.getStatusCode(), resp.getBody());
            throw new RuntimeException("chat message failed: " + resp.getStatusCode().value() + " body=" + resp.getBody());
        }

        JsonNode response = objectMapper.readTree(resp.getBody());
        log.info("Chat message response: {}", response);
        return response;
    }
}