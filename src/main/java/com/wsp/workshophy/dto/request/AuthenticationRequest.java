package com.wsp.workshophy.dto.request;

import com.wsp.workshophy.base.BaseRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest extends BaseRequest {
    String username;
    String password;
}
