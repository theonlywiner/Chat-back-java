package chatchatback.controller;

import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.vo.DifyEvaluateResultVO;
import chatchatback.service.DifyService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dify")
@RequiredArgsConstructor
@Slf4j
public class DifyController {

    private final DifyService difyService;
    private final PoemMapper poemMapper;

    /**
     * 上传视频并跑评分工作流，返回 Dify 响应
     */
    @PostMapping("/evaluate")
    public Result evaluate(@RequestParam("file") MultipartFile file, Long id) {

        Poem poem = poemMapper.getPoemById(id);
        String standardText = poem.getFullAncientContent();
        try {
            // 1. 上传文件
            JsonNode uploadResp = difyService.uploadFile(file);
            String uploadFileId = uploadResp.has("id") ? uploadResp.get("id").asText() : null;
            if (uploadFileId == null) {
                return Result.error("upload file failed: missing id");
            }

            // 2. 发送对话消息进行评分
            JsonNode chatResp = difyService.runWorkflow(standardText, uploadFileId);

            // 3. 解析响应 - 对话型应用的响应格式
            ObjectMapper mapper = new ObjectMapper();
            if (chatResp.has("answer")) {
                // 把 answer 字段的文本解析为 JSON 对象
                JsonNode answerNode = chatResp.get("answer");
                if (answerNode.isTextual()) {
                    String answerText = answerNode.asText();
                    try {
                        JsonNode parsed = mapper.readTree(answerText);
                        // 返回解析后的 JSON 对象/数组
                        return Result.success(parsed);
                    } catch (Exception ex) {
                        // 解析失败，退回到 text 字段
                        return Result.success(Map.of("text", answerText));
                    }
                } else {
                    // answer 已经是结构化 JSON，直接返回
                    return Result.success(answerNode);
                }
            } else if (chatResp.has("data") && chatResp.get("data").has("outputs")) {
                JsonNode outputs = chatResp.get("data").get("outputs");
                // outputs 可能已经是字符串或对象，直接转换为 VO 并返回
                DifyEvaluateResultVO vo;
                if (outputs.isTextual()) {
                    vo = mapper.readValue(outputs.asText(), DifyEvaluateResultVO.class);
                } else {
                    vo = mapper.treeToValue(outputs, DifyEvaluateResultVO.class);
                }
                return Result.success(vo);
            } else {
                // 返回完整响应用于调试
                return Result.success(chatResp);
            }
        } catch (Exception ex) {
            log.error("dify evaluate failed", ex);
            return Result.error("dify evaluate failed: " + ex.getMessage());
        }
    }
}