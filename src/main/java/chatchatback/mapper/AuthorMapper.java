package chatchatback.mapper;

import chatchatback.pojo.vo.AuthorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作者接口
 */
@Mapper
public interface AuthorMapper {


    /**
     * 获取所有作者
     * @return
     */
    @Select("select * from authors")
    List<AuthorVO> getAllAuthor();
}
