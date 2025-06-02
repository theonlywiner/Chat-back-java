package chatchatback.service;

import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.PoemPageQueryGradeDTO;
import chatchatback.pojo.entity.Dynasty;
import chatchatback.pojo.entity.Grade;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.vo.DailyPoemVO;
import chatchatback.pojo.vo.PoemListVO;


import java.util.List;

public interface PoemService {

    /**
     * 获取年级列表
     */
    List<Grade> getGradeList();

    /**
     * 根据年级id获取诗词列表
     */
    PageResult<PoemListVO> getPoemsByGradeId(PoemPageQueryGradeDTO poemPageQueryGradeDTO);

    /**
     * 根据诗词id获取诗词详情
     */
    Poem getPoemById(Long id);

    /**
     * 获取全部诗词列表
     */
    PageResult<PoemListVO>getAllPoems(PoemPageQueryDTO poemPageQueryDTO);

    /**
     * 根据ids获取诗词名称
     */
    List<PoemListVO> getPoemNameByIds(List<Long> ids);

    /**
     * 获取朝代列表
     */
    List<Dynasty> getDynasties();

    /**
     * 获取每日随机诗词
     */
    DailyPoemVO getDailyPoem();
}
