package com.wsp.workshophy.mapper;


import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileCreationRequest;
import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileUpdateRequest;
import com.wsp.workshophy.dto.response.OrganizerProfileResponse;
import com.wsp.workshophy.entity.OrganizerProfile;
import com.wsp.workshophy.entity.WorkshopCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizerProfileMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "categories", ignore = true)
    OrganizerProfile toOrganizerProfile(OrganizerProfileCreationRequest request);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "categories", target = "categoryNames", qualifiedByName = "mapCategoriesToNames")
    OrganizerProfileResponse toOrganizerProfileResponse(OrganizerProfile organizerProfile);

    @Mapping(target = "categories", ignore = true)
    void updateOrganizerProfileFromRequest(OrganizerProfileUpdateRequest request, @MappingTarget OrganizerProfile organizerProfile);

    @Named("mapCategoriesToNames")
    default List<String> mapCategoriesToNames(List<WorkshopCategory> categories) {
        return categories.stream()
                .map(WorkshopCategory::getName)
                .toList();
    }
}