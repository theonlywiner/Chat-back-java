package chatchatback.mapper;

import chatchatback.pojo.dto.LoginInfoDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    LoginInfoDTO getByUsername(String username);

    @Insert("insert into user(username, password, grade_id) values(#{username},#{encodedPassword},#{gradeId})")
    void addUser(String username, String encodedPassword, int gradeId);

    @Insert("update user set grade_id = #{gradeId} where id = #{id}")
    void updateGrade(int id, int gradeId);
}
