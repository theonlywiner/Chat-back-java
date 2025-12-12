package chatchatback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import chatchatback.pojo.entity.UserRecitationRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRecitationRecordMapper extends BaseMapper<UserRecitationRecord> {
	/**
	 * 插入记录（使用自定义 SQL，确保 created_at 使用数据库当前时间）
	 * @param record 记录实体
	 * @return 影响行数
	 */
	int insertRecord(UserRecitationRecord record);
    
	/**
	 * 根据当前线程的 userId (CurrentHolder) 查询该用户的所有背诵记录
	 */
	List<UserRecitationRecord> selectByCurrentUser(int userId);

}
