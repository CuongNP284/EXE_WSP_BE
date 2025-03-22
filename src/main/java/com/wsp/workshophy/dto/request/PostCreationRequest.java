package com.wsp.workshophy.dto.request;

import com.wsp.workshophy.base.BaseRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationRequest extends BaseRequest {
    String title;
    String description;
    String image;
    List<ParagraphRequest> paragraphs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    public static class ParagraphRequest extends BaseRequest {
        String content;
        String image;
    }
}
