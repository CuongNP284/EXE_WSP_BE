package com.wsp.workshophy.mapper;

import com.wsp.workshophy.constant.AdvertisementStatus;
import com.wsp.workshophy.dto.response.AdvertisementResponse;
import com.wsp.workshophy.entity.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper {
    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapStatusToString")
    AdvertisementResponse toAdvertisementResponse(Advertisement advertisement);

    @org.mapstruct.Named("mapStatusToString")
    default String mapStatusToString(AdvertisementStatus status) {
        return status.name();
    }
}
