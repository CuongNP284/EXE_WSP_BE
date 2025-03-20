package com.wsp.workshophy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.wsp.workshophy.dto.request.UserCreationRequest;
import com.wsp.workshophy.dto.request.UserUpdateRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

}
