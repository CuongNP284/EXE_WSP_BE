package com.wsp.workshophy.mapper;

import com.wsp.workshophy.dto.request.PostCreationRequest;
import com.wsp.workshophy.dto.response.PostResponse;
import com.wsp.workshophy.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "paragraphs", ignore = true)
    Post toPost(PostCreationRequest request);

    @Mapping(source = "author.username", target = "authorUsername")
    PostResponse toPostResponse(Post post);
}
