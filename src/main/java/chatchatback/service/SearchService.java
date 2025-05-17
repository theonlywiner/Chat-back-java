package chatchatback.service;

import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.entity.PoemListVO;
import chatchatback.pojo.vo.CountKeywordVO;

import java.util.List;

public interface SearchService {

    /**
     * 根据关键词和朝代搜索包含的古诗文
     * @param poemPageQueryDTO
     * @return
     */
    PageResult<PoemListVO> searchByKeywordAndDynasty(PoemPageQueryDTO poemPageQueryDTO);

    /**
     * 根据关键词返回根据朝代分组统计信息
     */
    List<CountKeywordVO> CountKeyWordByDynasty(PoemPageQueryDTO poemPageQueryDTO);
}
