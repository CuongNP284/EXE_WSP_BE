package com.wsp.workshophy.mapper;

import com.wsp.workshophy.entity.WorkshopCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.wsp.workshophy.dto.request.User.UserCreationRequest;
import com.wsp.workshophy.dto.request.User.UserUpdateRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.entity.User;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Map từ UserCreationRequest sang User
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.district", source = "district")
    @Mapping(target = "address.ward", source = "ward")
    @Mapping(target = "interests", ignore = true) // Interests sẽ được xử lý trong service
    User toUser(UserCreationRequest request);

    // Map từ User sang UserResponse
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.district", target = "district")
    @Mapping(source = "address.ward", target = "ward")
    @Mapping(source = "interests", target = "interestNames", qualifiedByName = "mapInterestsToNames")
    UserResponse toUserResponse(User user);

    @Named("mapInterestsToNames")
    default List<String> mapInterestsToNames(List<WorkshopCategory> interests) {
        if (interests == null) {
            return List.of();
        }
        return interests.stream()
                .map(WorkshopCategory::getName)
                .toList();
    }
}