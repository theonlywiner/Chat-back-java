package chatchatback.mapper;

import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.PoemPageQueryGradeDTO;
import chatchatback.pojo.entity.Dynasty;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.vo.CountKeywordVO;
import chatchatback.pojo.vo.PoemListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    Long countAllPoems(PoemPageQueryDTO poemPageQueryDTO);

    // 根据条件查找分页起始ID
    Long findOffsetId(PoemPageQueryDTO poemPageQueryDTO);

    // 根据ids获取诗词名
    List<PoemListVO> getPoemNameByIds(List<Long> ids);

    // 根据关键词搜索
    List<PoemListVO> searchByKeyword(PoemPageQueryDTO poemPageQueryDTO);

    // 根据关键词搜索返回朝代对应的古诗文个数
    List<CountKeywordVO> SearchCountByKeyword(PoemPageQueryDTO poemPageQueryDTO);

    // 获取朝代列表
    @Select("select dynasty.id, dynasty.name from dynasty order by dynasty.id")
    List<Dynasty> getDynasties();

    //随机获取一首诗
    Poem getRandomPoem();
}
