package com.wsp.workshophy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvertisementResponse {
    private Long id;
    private String image;
    private Long duration;
    private LocalDateTime expirationTime;
    private LocalDateTime approvedAt;
    private String status;
    private boolean active;
    private String organizerId;
}
