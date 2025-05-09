package chatchatback.service.impl;

import chatchatback.mapper.GradeMapper;
import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.PoemPageQueryGradeDTO;
import chatchatback.pojo.entity.Grade;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.entity.PoemListVO;
import chatchatback.service.PoemService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoemServiceImpl implements PoemService {

    @Autowired
    private PoemMapper poemMapper;
    @Autowired
    private GradeMapper gradeMapper;

    /**
     * 获取年级列表
     */
    @Override
    public List<Grade> getGradeList() {
        List<Grade> gradeList = gradeMapper.getGradeList();
        Grade grade = new Grade(0L, "全部");
        gradeList.addFirst(grade);
        return gradeList;
    }

    /**
     * 根据年级id获取诗词列表
     */
    @Override
    public PageResult<PoemListVO> getPoemsByGradeId(PoemPageQueryGradeDTO poemPageQueryGradeDTO) {
        //设置分页信息
        PageHelper.startPage(poemPageQueryGradeDTO.getPage(), poemPageQueryGradeDTO.getPageSize());

        //执行查询
        Page<PoemListVO> poemList = (Page<PoemListVO>)poemMapper.getPoemsByGradeId(poemPageQueryGradeDTO);
        return new PageResult<>(poemList.getTotal(), poemList.getResult());
    }

    /**
     * 根据id获取诗词
     */
    @Override
    public Poem getPoemById(Long id) {
        return poemMapper.getPoemById(id);
    }

    /**
     * 获取所有诗词，分页
     */
    @Override
    public PageResult<PoemListVO> getAllPoems(PoemPageQueryDTO dto) {
        // 动态计算lastId
        calculateLastId(dto);

        // 分页查询
        List<PoemListVO> list = poemMapper.getAllPoems(dto);

        // 总数查询（带条件）
        Long total = poemMapper.countAllPoems(dto.getName(), dto.getGradeId());

        return new PageResult<>(total, list);
    }

    /**
     * 根据id列表获取诗词名称
     */
    @Override
    public List<PoemListVO> getPoemNameByIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return poemMapper.getPoemNameByIds(ids);
        }
        return null;
    }

    private void calculateLastId(PoemPageQueryDTO dto) {
        if (dto.getPage() == 1) {
            dto.setLastId(0L);
            return;
        }

        // 动态查询上一页最后ID
        Long offset = (long) (dto.getPage() - 1) * dto.getPageSize();
        dto.setLastId(poemMapper.findOffsetId(
                dto.getName(),
                dto.getGradeId(),
                offset
        ));
    }
}
