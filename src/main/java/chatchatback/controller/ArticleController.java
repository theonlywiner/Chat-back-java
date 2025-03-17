package chatchatback.controller;

import chatchatback.pojo.*;
import chatchatback.service.BookService;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ArticleController {

    @Autowired
    private BookService bookService;

    /**
     *  1. 根据chapter的id获取文章详情
     * */
    @GetMapping("/getArticleDetail")
    public Result getArticleDetail(
            @RequestParam @Min(0) Long id) {

        try {
            Map<String, Object> result = new HashMap<>();
            // 获取文章详情
            ClassicPoemInfo poemInfo = bookService.getClassicPoemInfo(id);
            // 获取导航树
            List<NavTreeData> navTree = bookService.getNavTree(id);

            result.put("result", poemInfo);
            result.put("navTreeData", navTree);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error( "系统错误: " + e.getMessage());
        }
    }

    /**
     *  2.1 查询所有chapter数据，搜索列表展示
     * */
    @GetMapping("/getArticleList")
    public Result getArticleList(SearchQueryParam param) {
        try {
            log.info("文章列表查询：{}", param);
            PageResult<ClassicPoemInfo> result = bookService.page(param);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询失败：", e);
            return Result.error("系统错误：" + e.getMessage());
        }
    }

    /**
     *  2.2 携带title查询
     * */
    @GetMapping("/searchArticles")
    public Result searchArticle(SearchQueryParam param) {
        try {
            log.info("文章title查询：{}", param);
            PageResult<ClassicPoemInfo> result = bookService.searchTitle(param);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询失败：", e);
            return Result.error("系统错误：" + e.getMessage());
        }
    }
}
