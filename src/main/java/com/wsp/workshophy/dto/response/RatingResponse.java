package com.wsp.workshophy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingResponse {
    Long id;
    String username;
    Long organizerProfileId;
    String organizerProfileName;
    Double rating;
}
