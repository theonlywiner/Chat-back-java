package chatchatback.utils;

import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageQueryUtils {

    @Autowired
    private PoemMapper poemMapper;

    public void calculateLastId(PoemPageQueryDTO dto) {
        if (dto.getPage() == 1) {
            dto.setLastId(0L);
            return;
        }

        // 动态查询上一页最后ID
        Long offset = (long) (dto.getPage() - 1) * dto.getPageSize();
        dto.setOffset(offset);
        dto.setLastId(poemMapper.findOffsetId(dto));
    }

}
