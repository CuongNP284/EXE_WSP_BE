package com.wsp.workshophy.dto.response;

import com.wsp.workshophy.base.BaseRepsonse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizerProfileResponse {
    Long id;
    String name;
    String description;
    String theme;
    Integer followerCount;
    Integer workshopCount;
    List<String> categoryNames;
    String username;
}
