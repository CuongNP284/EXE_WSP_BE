package com.wsp.workshophy.dto.response;

import com.wsp.workshophy.base.BaseRepsonse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizerProfileResponse extends BaseRepsonse {
    Long id;
    String name;
    String description;
    String avatar;
    String theme;
    Integer followerCount;
    Integer workshopCount;
    List<String> categoryNames;
    String username;
    Double averageRating;
    LocalDateTime createdDate;
    LocalDate establishmentDate;
    String tenure;
    List<OrganizerProfileResponse> relatedProfiles;
    List<OrganizerRatedByUserResponse> ratings;
    String userId;
}
