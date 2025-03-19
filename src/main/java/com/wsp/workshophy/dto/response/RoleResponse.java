package com.wsp.workshophy.dto.response;

import java.util.Set;

import com.wsp.workshophy.base.BaseRepsonse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse extends BaseRepsonse {
    String name;
    String description;
}
