package chatchatback.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 重点：继承 BaseTypeHandler 并指定泛型
public class JsonListTypeHandler extends BaseTypeHandler<List<Integer>> {
    private static final ObjectMapper mapper = new ObjectMapper();

    //------------------------ 写入数据库 ------------------------
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    List<Integer> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, listToJson(parameter)); // 将 List 转为 JSON 字符串
    }

    private String listToJson(List<Integer> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("非法的JSON数据", e);
        }
    }

    //------------------------ 从数据库读取 ------------------------
    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return jsonToList(rs.getString(columnName));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return jsonToList(rs.getString(columnIndex));
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return jsonToList(cs.getString(columnIndex));
    }

    private List<Integer> jsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(json, new TypeReference<List<Integer>>(){});
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败", e);
        }
    }
}
