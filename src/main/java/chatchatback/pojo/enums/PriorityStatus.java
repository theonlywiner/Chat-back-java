package chatchatback.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PriorityStatus {
    HIGH_FREQUENCY(1, "高频重点词"),
    MEDIUM_FREQUENCY(2, "中频重要词"),
    LOW_FREQUENCY(3, "低频重要词"),
    TEST_FREQUENCY(4, "测试词"),
    ;

    @EnumValue    //表示这个字段作为数据库中存放的值，后续查询和保存都用这个值来处理
    private final int value;
    @JsonValue        //如果不加这个字段，默认会向前端返回NORMAL/FREEZE，加了这个字段会返回这个字段的值
    private final String priority;

    PriorityStatus(int value, String priority) {
        this.value = value;
        this.priority = priority;
    }

    // 根据输入重要性字符，查找获取枚举
    public static PriorityStatus fromPriority(String priority) {
        for (PriorityStatus status : values()) {
            if (status.priority.equals(priority)) {
                return status;
            }
        }
        return null;
    }
}