package chatchatback.controller;

import chatchatback.constant.MessageConstant;
import chatchatback.pojo.dto.ConversationRenameDTO;
import chatchatback.pojo.vo.dify.ConversationListVO;
import chatchatback.pojo.vo.dify.ConversationVO;
import chatchatback.pojo.vo.dify.MessageListVO;
import chatchatback.pojo.dto.Result;
import chatchatback.service.DifyHistoryService;
import chatchatback.utils.CurrentHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/dify/history")
@RequiredArgsConstructor
public class DifyHistoryController {

    private final DifyHistoryService difyHistoryService;

    /**
     * 获取当前用户的会话列表
     */
    @GetMapping("/conversations")
    public Result getConversations(
            @RequestParam(required = false) String last_id,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            // 直接从 ThreadLocal 获取用户ID
            Integer currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                return Result.error(MessageConstant.ACCOUNT_NOT_AUTHENTICATED);
            }
            log.info("获取用户{}会话列表..", currentUserId);

            String difyUserId = "user_" + currentUserId;
            ConversationListVO response = difyHistoryService.getConversations(difyUserId, last_id, limit);
            return Result.success(response);

        } catch (Exception e) {
            log.error("获取会话列表失败: {}", e.getMessage());
            return Result.error("获取会话列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取会话的详细消息历史
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public Result getMessages(
            @PathVariable String conversationId,
            @RequestParam(required = false) String first_id,
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            Integer currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                return Result.error(MessageConstant.ACCOUNT_NOT_AUTHENTICATED);
            }

            String difyUserId = "user_" + currentUserId;
            MessageListVO response = difyHistoryService.getMessages(conversationId, difyUserId, first_id, limit);
            return Result.success(response);

        } catch (Exception e) {
            log.error("获取消息历史失败: {}", e.getMessage());
            return Result.error("获取消息历史失败: " + e.getMessage());
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public Result deleteConversation(@PathVariable String conversationId) {
        try {
            Integer currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                return Result.error(MessageConstant.ACCOUNT_NOT_AUTHENTICATED);
            }

            String difyUserId = "user_" + currentUserId;
            difyHistoryService.deleteConversation(conversationId, difyUserId);
            return Result.success("删除成功");

        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage());
            return Result.error("删除会话失败: " + e.getMessage());
        }
    }

    /**
     * 重命名会话
     */
    @PostMapping("/conversations/{conversationId}/rename")
    public Result renameConversation(
            @PathVariable String conversationId,
            @Valid @RequestBody ConversationRenameDTO renameDTO) {
        try {
            Integer currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                return Result.error(MessageConstant.ACCOUNT_NOT_AUTHENTICATED);
            }

            String difyUserId = "user_" + currentUserId;
            ConversationVO response = difyHistoryService.renameConversation(
                    conversationId, renameDTO.getName(), renameDTO.getAuto_generate(), difyUserId
            );
            return Result.success(response);

        } catch (Exception e) {
            log.error("重命名会话失败: {}", e.getMessage());
            return Result.error("重命名会话失败: " + e.getMessage());
        }
    }
}