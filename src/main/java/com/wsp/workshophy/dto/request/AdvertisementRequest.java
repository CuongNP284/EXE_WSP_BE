package com.wsp.workshophy.dto.request;

import com.wsp.workshophy.base.BaseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvertisementRequest extends BaseRequest {
    @NotBlank(message = "Image URL must not be blank")
    private String image;

    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Long duration; // Thời hạn (tính bằng giờ)
}
