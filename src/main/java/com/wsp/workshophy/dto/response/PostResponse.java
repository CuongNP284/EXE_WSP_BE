package com.wsp.workshophy.dto.response;

import com.wsp.workshophy.base.BaseRepsonse;
import com.wsp.workshophy.constant.PostStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse extends BaseRepsonse {
    Long id;
    String title;
    String description;
    String image;
    PostStatus status;
    String authorUsername;
    List<ParagraphResponse> paragraphs;
    LocalDateTime createdDate;
}
