package chatchatback.service;

import chatchatback.pojo.dto.SearchQueryParamDTO;
import chatchatback.pojo.entity.ClassicPoemInfo;
import chatchatback.pojo.vo.NavTreeDataVO;
import chatchatback.pojo.dto.PageResult;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

public interface BookService {
    //1.首页书籍信息获取
    List<ClassicPoemInfo> getAllBooks();

    //2.书籍详情基本信息获取
    ClassicPoemInfo getClassicPoemInfo(@Min(0) Long  id) throws NotFoundException;

    //3.书籍详情页导航树获取
    List<NavTreeDataVO> getNavTree(@Min(0) Long  id);

    //4.搜素列表
    PageResult<ClassicPoemInfo> page(SearchQueryParamDTO searchQueryParam);

    PageResult<ClassicPoemInfo> searchTitle(SearchQueryParamDTO param);
}
