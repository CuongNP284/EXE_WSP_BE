package com.wsp.workshophy.mapper;


import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileCreationRequest;
import com.wsp.workshophy.dto.response.OrganizerProfileResponse;
import com.wsp.workshophy.entity.OrganizerProfile;
import com.wsp.workshophy.entity.WorkshopCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizerProfileMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "categories", ignore = true)
    OrganizerProfile toOrganizerProfile(OrganizerProfileCreationRequest request);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "categories", target = "categoryNames", qualifiedByName = "mapCategoriesToNames")
    @Mapping(source = "establishmentDate", target = "tenure", qualifiedByName = "calculateTenure")
    @Mapping(source = "user.id", target = "userId")
    OrganizerProfileResponse toOrganizerProfileResponse(OrganizerProfile organizerProfile);


    @Named("mapCategoriesToNames")
    default List<String> mapCategoriesToNames(List<WorkshopCategory> categories) {
        return categories.stream()
                .map(WorkshopCategory::getName)
                .toList();
    }

    @Named("calculateTenure")
    default String calculateTenure(LocalDate establishmentDate) {
        if (establishmentDate == null) {
            return "0 năm, 0 tháng, 0 ngày";
        }
        LocalDate currentDate = LocalDate.now(); // Sử dụng ngày hiện tại thực tế
        Period period = Period.between(establishmentDate, currentDate);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        return String.format("%d năm, %d tháng, %d ngày", years, months, days);
    }
}