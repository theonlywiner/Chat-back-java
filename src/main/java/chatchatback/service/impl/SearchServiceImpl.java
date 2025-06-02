package chatchatback.service.impl;

import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.vo.CountKeywordVO;
import chatchatback.pojo.vo.PoemListVO;
import chatchatback.service.SearchService;
import chatchatback.utils.PageQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PoemMapper poemMapper;

    @Autowired
    private PageQueryUtils pageQueryUtils;


    /**
     * 根据关键词搜索包含的古诗文
     * @param poemPageQueryDTO
     * @return
     */
    @Override
    public PageResult<PoemListVO> searchByKeywordAndDynasty(PoemPageQueryDTO poemPageQueryDTO) {
        //动态计算lastId
        pageQueryUtils.calculateLastId(poemPageQueryDTO);

        // 分页查询
        List<PoemListVO> list = poemMapper.searchByKeyword(poemPageQueryDTO);

        // 总数查询（带条件）
        Long total = poemMapper.countAllPoems(poemPageQueryDTO);

        return new PageResult<>(total, list);
    }

    /**
     * 根据关键词搜索包含的古诗文数量
     */
    @Override
    public List<CountKeywordVO> CountKeyWordByDynasty(PoemPageQueryDTO poemPageQueryDTO) {
        return poemMapper.SearchCountByKeyword(poemPageQueryDTO);
    }
}
