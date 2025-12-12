package chatchatback.service;

import chatchatback.pojo.entity.UserRecitationRecord;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DifyService {
    JsonNode uploadFile(MultipartFile file) throws Exception;
    JsonNode runWorkflow(Long poemId, String standardText, String uploadFileId) throws Exception;

    /**
     * 查询当前用户的所有背诵记录（无需传参），根据 CurrentHolder 中的 userId
     */
    List<UserRecitationRecord> getMyRecords();
}
