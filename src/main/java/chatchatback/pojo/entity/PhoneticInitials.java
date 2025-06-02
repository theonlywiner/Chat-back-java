package chatchatback.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneticInitials {
    private String initial;
    private String displayName;
//    private Integer sortOrder;
}
