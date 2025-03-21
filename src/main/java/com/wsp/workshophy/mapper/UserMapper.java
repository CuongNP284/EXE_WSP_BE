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
    // Map từ UserCreationRequest sang User
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.district", source = "district")
    @Mapping(target = "address.ward", source = "ward")
    User toUser(UserCreationRequest request);

    // Map từ User sang UserResponse
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.district", target = "district")
    @Mapping(source = "address.ward", target = "ward")
    UserResponse toUserResponse(User user);

    // Map từ UserUpdateRequest để cập nhật User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "address", ignore = true) // Address sẽ được xử lý riêng trong service
    @Mapping(target = "roles", ignore = true)
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);
}