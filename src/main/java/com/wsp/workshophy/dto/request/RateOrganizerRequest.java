package com.wsp.workshophy.dto.request;

import com.wsp.workshophy.base.BaseRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateOrganizerRequest extends BaseRequest {
    @NotNull(message = "Rating must not be null")
    @Min(value = 1, message = "Rating must be at least 1.0")
    @Max(value = 5, message = "Rating must be at most 5.0")
    Double rating;

    String comment;
}
