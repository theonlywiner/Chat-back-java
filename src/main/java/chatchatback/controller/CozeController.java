package chatchatback.controller;

import chatchatback.pojo.dto.Result;
import chatchatback.pojo.entity.UserRecitationRecord;
import chatchatback.service.CozeService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Coze 背诵评估模块
 */
@RestController
@RequestMapping("/coze")
@RequiredArgsConstructor
@Slf4j
public class CozeController {

    private final CozeService cozeService;

    /**
     * 上传语音文件并评估古诗文背诵
     * @param file 背诵的语音文件
     * @param id 古诗文ID
     * @return 评估结果（标准 JSON 格式）
     */
    @PostMapping("/evaluate")
    public Result evaluate(@RequestParam("file") MultipartFile file, 
                          @RequestParam("id") Long id) {
        try {
            log.info("收到背诵评估请求，古诗文ID: {}, 文件名: {}", id, file.getOriginalFilename());
            
            // 验证参数
            if (file.isEmpty()) {
                return Result.error("语音文件不能为空");
            }
            
            if (id == null) {
                return Result.error("古诗文ID不能为空");
            }

            // 调用 Coze 工作流进行评估
            JsonNode result = cozeService.evaluateRecitation(file, id);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("背诵评估失败：", e);
            return Result.error("系统错误：" + e.getMessage());
        }
    }
}
