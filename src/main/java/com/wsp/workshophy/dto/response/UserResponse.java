package com.wsp.workshophy.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.wsp.workshophy.base.BaseRepsonse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse{
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;

    String street;
    String city;
    String district;
    String ward;
    Set<RoleResponse> roles;
    List<String> interestNames;
}
