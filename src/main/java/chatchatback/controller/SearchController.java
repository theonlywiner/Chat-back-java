package chatchatback.controller;

import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.Result;
import chatchatback.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 根据关键词和朝代（可有可无）搜索古诗
     */
    @GetMapping("/keyword")
    public Result SearchByKeyWord(PoemPageQueryDTO poemPageQueryDTO) {
        log.info("处理用户搜索词语分页信息,{}", poemPageQueryDTO);
        return Result.success(searchService.searchByKeywordAndDynasty(poemPageQueryDTO));
    }

    /**
     * 根据关键词返回朝代统计信息
     */
    @GetMapping("/dynastiesCount")
    public Result CountKeyWordByDynasty(PoemPageQueryDTO poemPageQueryDTO) {
        log.info("处理用户搜索词语: {}", poemPageQueryDTO);
        return Result.success(searchService.CountKeyWordByDynasty(poemPageQueryDTO));
    }


}
