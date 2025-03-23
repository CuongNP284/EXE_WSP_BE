package com.wsp.workshophy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizerRatedByUserResponse {
    String username;
    Double rating;
    String comment;
    LocalDateTime createdDate;
}
