package chatchatback.controller;

import chatchatback.pojo.dto.Result;
import chatchatback.pojo.dto.WordSearchDTO;
import chatchatback.service.WordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 常用词控制层
 */
@Slf4j
@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    private WordService wordService;

    /**
     * 搜索常用词
     */
    @GetMapping("/searchWord")
    public Result searchWord(WordSearchDTO wordSearchDTO) {
        log.info("searchWord: {}", wordSearchDTO);
        return Result.success(wordService.searchWord(wordSearchDTO));
    }

    /**
     * 搜索常用词分类标准（目前只有字母）
     */
    @GetMapping("/searchWordClass")
    public Result searchWordClass() {
        log.info("searchWordClass...");
        return Result.success(wordService.searchWordClass());
    }
}
