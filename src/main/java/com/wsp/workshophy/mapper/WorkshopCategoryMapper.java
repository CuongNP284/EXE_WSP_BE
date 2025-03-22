package com.wsp.workshophy.mapper;


import com.wsp.workshophy.dto.request.WorkshopCategory.WorkshopCategoryCreationRequest;
import com.wsp.workshophy.dto.request.WorkshopCategory.WorkshopCategoryUpdateRequest;
import com.wsp.workshophy.dto.response.WorkshopCategoryResponse;
import com.wsp.workshophy.entity.WorkshopCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkshopCategoryMapper {
    WorkshopCategory toWorkshopCategory(WorkshopCategoryCreationRequest request);

    WorkshopCategoryResponse toWorkshopCategoryResponse(WorkshopCategory category);

    void updateWorkshopCategoryFromRequest(WorkshopCategoryUpdateRequest request, @MappingTarget WorkshopCategory category);
}