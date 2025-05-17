package chatchatback.controller;

import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.PoemPageQueryGradeDTO;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.entity.Grade;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.entity.PoemListVO;
import chatchatback.service.PoemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PoemController {

    @Autowired
    private PoemService poemService;

    /**
     * 获取所有诗词列表分页，通过是否有gradeId来判断是否年级查询古诗
     */
    @GetMapping("/poems")
    public Result getPoems(PoemPageQueryDTO poemPageQueryDTO) {
        log.info("获取所有诗词列表分页,{}", poemPageQueryDTO);
        PageResult<PoemListVO> list = poemService.getAllPoems(poemPageQueryDTO);
        return Result.success(list);
    }

    /**
     * 获取年级列表
     */
    @GetMapping("/getGradeList")
    public Result getGradeList() {
        log.info("获取年级列表...");
        List<Grade> list = poemService.getGradeList();
        return Result.success(list);
    }

    /**
     * 根据年级获取诗词列表
     */
    @GetMapping("/poemsByGrade")
    public Result getPoemsByGradeId(PoemPageQueryGradeDTO poemPageQueryGradeDTO) {
        log.info("根据年级获取诗词列表,{}", poemPageQueryGradeDTO);
        PageResult<PoemListVO> list = poemService.getPoemsByGradeId(poemPageQueryGradeDTO);
        return Result.success(list);
    }

    /**
     * 根据诗词id获取诗词详情
     */
    @GetMapping("/poem/{id}")
    public Result getPoemById(@PathVariable Long id) {
        log.info("根据诗词id获取诗词详情,{}",  id);
        Poem poem = poemService.getPoemById(id);
        return Result.success(poem);
    }

    /**
     * 根据诗歌id列表获取诗词名
     */
    @GetMapping("/poemsName")
    public Result getPoemNameByIds(@RequestParam List<Long> ids) {
        log.info("根据诗歌id列表获取诗词名,{}",  ids);
        List<PoemListVO> list = poemService.getPoemNameByIds(ids);
        return Result.success(list);
    }

    /**
     * 获取朝代列表
     */
    @GetMapping("/dynasties")
    public Result getDynasties() {
        log.info("获取朝代列表...");
        return Result.success(poemService.getDynasties());
    }
}
