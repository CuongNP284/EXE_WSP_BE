package com.wsp.workshophy.dto.request.OrganizerProfile;

import com.wsp.workshophy.base.BaseRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizerProfileUpdateRequest extends BaseRequest {
    String name;
    String description;
    String theme;
    Integer followerCount;
    Integer workshopCount;
    List<String> categoryNames;
    LocalDate establishmentDate;
}
