package com.wsp.workshophy.dto.request;

import java.util.Set;

import com.wsp.workshophy.base.BaseRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest extends BaseRequest {
    String name;
    String description;
}
