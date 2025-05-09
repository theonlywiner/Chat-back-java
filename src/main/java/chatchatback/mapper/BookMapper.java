package chatchatback.mapper;

import chatchatback.pojo.dto.SearchQueryParamDTO;
import chatchatback.pojo.entity.ClassicPoemInfo;
import chatchatback.pojo.dto.ContentPair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {
    //首页书籍信息获取
    List<ClassicPoemInfo> selectAllBooksOptimized();

    //根据id查询chapter表的数据
    ClassicPoemInfo selectPoemDetailById(Long  chapterId);

    //根据id查询paragraphs表的数据，每一行古文和翻译
    List<ContentPair> selectContentPairsById(Long  chapterId);

    //根据chapterId查询关联book表的数据
    Map<String, Object> selectChapterRelations(Long  chapterId);

    //根据seriesId查询service的数据
    Map<String, Object> selectSeriesById(Long  seriesId);

    //查询book跟service关联的数据
    List<Map<String, Object>> selectBooksBySeries(Long  seriesId);

    //查询book跟chapter的关联数据
    List<Map<String, Object>> selectChaptersByBook(Long  id);

    //查询book表的数据
    Map<String, Object> selectBookById(Long  bookId);

    //搜索分页查询(传入开始索引和结束索引)
    List<ClassicPoemInfo> list(SearchQueryParamDTO searchQueryParam);

    //查询总数
    @Select("select count(*) from chapters")
    Long countTotal();
}