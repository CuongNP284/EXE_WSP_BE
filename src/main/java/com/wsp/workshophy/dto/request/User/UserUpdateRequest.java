package com.wsp.workshophy.dto.request.User;

import java.time.LocalDate;
import java.util.List;

import com.wsp.workshophy.base.BaseRequest;
import com.wsp.workshophy.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest extends BaseRequest {
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;

    String street;
    String city;
    String district;
    String ward;

    List<String> roles;
    List<String> interestNames;
}
