package chatchatback.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DifficultyStatus {
//    '难度：1-初级, 2-中级, 3-高级'
    EASY(1, "初级"),
    MEDIUM(2, "中级"),
    HARD(3, "高级");

    @EnumValue    //表示这个字段作为数据库中存放的值，后续查询和保存都用这个值来处理
    private final int value;
    @JsonValue        //如果不加这个字段，默认会向前端返回NORMAL/FREEZE，加了这个字段会返回这个字段的值
    private final String difficulty;

    DifficultyStatus(int value, String difficulty) {
        this.value = value;
        this.difficulty = difficulty;
    }

    // 根据输入难度字符或数字，查找获取枚举
    @JsonCreator
    public static DifficultyStatus from(Object value) {
        if (value instanceof String) {
            for (DifficultyStatus status : values()) {
                if (status.difficulty.equals(value)) {
                    return status;
                }
            }
        } else if (value instanceof Number) {
            int intValue = ((Number) value).intValue();
            for (DifficultyStatus status : values()) {
                if (status.value == intValue) {
                    return status;
                }
            }
        }
        return null;
    }

}