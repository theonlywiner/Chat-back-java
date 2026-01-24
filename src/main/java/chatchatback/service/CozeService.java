package chatchatback.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

public interface CozeService {
    /**
     * 调用 Coze 工作流评估古诗文背诵
     * @param file 语音文件
     * @param poemId 古诗文ID
     * @return 评估结果
     */
    JsonNode evaluateRecitation(MultipartFile file, Long poemId) throws Exception;
}
