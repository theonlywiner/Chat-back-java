package chatchatback.controller;

import chatchatback.constant.ServiceConstant;
import chatchatback.pojo.dto.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

import static chatchatback.constant.MessageConstant.UPLOAD_FILE_EMPTY;

@RestController
@RequestMapping("/whisper")
@Slf4j
public class WhisperController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/transcribe")
    public Result transcribe(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "uploadId", required = false) String uploadId,
            @RequestParam(value = "partIndex", required = false) Integer partIndex,
            @RequestParam(value = "totalParts", required = false) Integer totalParts
    ) {
        log.info("transcribe file: {}, uploadId: {}, partIndex: {}, totalParts: {}", file, uploadId, partIndex, totalParts);

        try {
            byte[] bytes;
            String filename;

            // uploadId为null表示单文件上传
            if (uploadId == null) {
                // 单文件上传
                if (file == null || file.isEmpty()) return Result.error(UPLOAD_FILE_EMPTY);
                bytes = file.getBytes();
                filename = file.getOriginalFilename();
            } else {
                // 分片上传
                if (partIndex == null || totalParts == null) return Result.error("partIndex and totalParts required for chunk upload");

                Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"), "whisper_chunks", uploadId);
                Files.createDirectories(tmpDir);

                // 保存分片
                Path partPath = tmpDir.resolve(String.format("part_%05d", partIndex));
                try (InputStream in = file.getInputStream()) {
                    Files.write(partPath, in.readAllBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                }

                // 如果所有分片已到达，合并
                boolean all = true;
                for (int i = 1; i <= totalParts; i++) {
                    if (!Files.exists(tmpDir.resolve(String.format("part_%05d", i)))) { all = false; break; }
                }
                if (!all) return Result.success("part received");

                // 合并
                Path merged = tmpDir.resolve(uploadId + "_merged");
                try (var out = Files.newOutputStream(merged, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    for (int i = 1; i <= totalParts; i++) {
                        Path p = tmpDir.resolve(String.format("part_%05d", i));
                        out.write(Files.readAllBytes(p));
                    }
                }
                bytes = Files.readAllBytes(merged);
                filename = merged.getFileName().toString();

                // 清理
                try {
                    Files.walk(tmpDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(java.io.File::delete);
                } catch (Exception ex) {
                    log.warn("清理分片失败: {}", ex.getMessage());
                }
            }

            // 构建 multipart 请求并转发
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            body.add("file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(ServiceConstant.WHISPER_SERVICE_URL, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return Result.error("whisperService服务失败: " + response.getStatusCode().value());
            }

            // 解析返回 JSON，取 text 字段
            JsonNode root = objectMapper.readTree(response.getBody());
            log.info("whisper service response: {}", root);
            String text = null;
            if (root.has("text")) text = root.get("text").asText();
            log.info("transcribe result: {}", text);
            return Result.success(text == null ? "" : text);

        } catch (IOException ex) {
            log.error("transcribe io error", ex);
            return Result.error("transcribe io error: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("transcribe error", ex);
            return Result.error("transcribe error: " + ex.getMessage());
        }
    }
}
