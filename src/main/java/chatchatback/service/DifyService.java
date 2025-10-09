package chatchatback.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

public interface DifyService {
    JsonNode uploadFile(MultipartFile file) throws Exception;
    JsonNode runWorkflow(String standardText, String uploadFileId) throws Exception;
}
