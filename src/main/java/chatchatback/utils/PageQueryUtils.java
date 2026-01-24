package chatchatback.utils;

import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageQueryUtils {

    @Autowired
    private PoemMapper poemMapper;

    // 修改calculateLastId
    public void calculateLastId(PoemPageQueryDTO dto) {
        // 参数验证
        if (dto.getPage() == null || dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getPageSize() == null || dto.getPageSize() < 1) {
            dto.setPageSize(10);
        }

        // 计算offset
        Long offset = (long) (dto.getPage() - 1) * dto.getPageSize();
        dto.setOffset(Math.max(0, offset));
    }

}
