package com.wsp.workshophy.dto.request;

import com.wsp.workshophy.base.BaseRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectRequest extends BaseRequest {
    String token;
}
