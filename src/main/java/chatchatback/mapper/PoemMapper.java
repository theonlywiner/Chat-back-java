package chatchatback.mapper;

import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.PoemPageQueryGradeDTO;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.entity.PoemListVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PoemMapper {
    //根据年级id获取诗词列表,分页
    List<PoemListVO> getPoemsByGradeId(PoemPageQueryGradeDTO poemPageQueryGradeDTO);

    //根据id获取诗词
    Poem getPoemById(Long id);

    //获取所有诗词，分页
    List<PoemListVO> getAllPoems(PoemPageQueryDTO poemPageQueryDTO);

    // 新增总数统计方法
    Long countAllPoems(String name, Long gradeId);

    // 根据条件查找分页起始ID
    Long findOffsetId(String name, Long gradeId, Long offset);

    // 根据ids获取诗词名
    List<PoemListVO> getPoemNameByIds(List<Long> ids);
}
