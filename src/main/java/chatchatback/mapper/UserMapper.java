package chatchatback.mapper;

import chatchatback.pojo.LoginInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    LoginInfo findByUsername(String username);

    @Insert("insert into user(username,password) values(#{username},#{encodedPassword})")
    void addUser(String username, String encodedPassword);
}
