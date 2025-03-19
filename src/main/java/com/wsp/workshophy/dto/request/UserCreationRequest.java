package com.wsp.workshophy.dto.request;

import java.time.LocalDate;

import com.wsp.workshophy.base.BaseRequest;
import jakarta.validation.constraints.Size;

import com.wsp.workshophy.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest extends BaseRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    String email;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

    @DobConstraint(min = 10, message = "INVALID_DOB")
    LocalDate dob;

    String street;
    String city;
    String district;
    String ward;
}
