package chatchatback.controller;

import chatchatback.pojo.dto.Result;
import chatchatback.pojo.vo.AuthorProfileVO;
import chatchatback.pojo.vo.AuthorVO;
import chatchatback.pojo.vo.AuthorWordCloudVO;
import chatchatback.service.AuthorService;
import chatchatback.service.WordCloudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visualization")
@Slf4j
public class VisualizationController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private WordCloudService wordCloudService;

    /**
     * 获取所有作者数据
     */
    @GetMapping("/getAllAuthor")
    public Result getAllAuthor() {
        log.info("获取所有作者数据");
        List<AuthorVO> authors = authorService.getAllAuthor();
        return Result.success(authors);
    }

    /**
     * 获取单个作者常用词
     */
    @GetMapping("/getAuthorInfo")
    public Result getAuthorInfo(Long id) {
        log.info("获取单个作者常用词,{}",id);
        AuthorProfileVO author = authorService.getAuthorInfo(id);
        return Result.success(author);
    }

    /**
     * 获取单个作者词云信息
     */
    @GetMapping("/getAuthorWordCloud")
    public Result getAuthorWordCloud(Long id) {
        log.info("获取单个作者词云信息,{}",id);
       AuthorWordCloudVO authorWordCloudVO = wordCloudService.getAuthorWordCloud(id);
        return Result.success(authorWordCloudVO);
    }
}
