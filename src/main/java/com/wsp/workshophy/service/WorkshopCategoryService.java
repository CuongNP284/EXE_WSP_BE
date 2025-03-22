package com.wsp.workshophy.service;

import com.wsp.workshophy.dto.request.WorkshopCategory.WorkshopCategoryCreationRequest;
import com.wsp.workshophy.dto.request.WorkshopCategory.WorkshopCategoryUpdateRequest;
import com.wsp.workshophy.dto.response.WorkshopCategoryResponse;
import com.wsp.workshophy.entity.WorkshopCategory;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.WorkshopCategoryMapper;
import com.wsp.workshophy.repository.WorkshopCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkshopCategoryService {
    WorkshopCategoryRepository workshopCategoryRepository;
    WorkshopCategoryMapper workshopCategoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public WorkshopCategoryResponse createWorkshopCategory(WorkshopCategoryCreationRequest request) {
        WorkshopCategory category = workshopCategoryMapper.toWorkshopCategory(request);
        return workshopCategoryMapper.toWorkshopCategoryResponse(workshopCategoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public WorkshopCategoryResponse updateWorkshopCategory(Long id, WorkshopCategoryUpdateRequest request) {
        WorkshopCategory category = findWorkshopCategoryByIdAndActive(id);
        workshopCategoryMapper.updateWorkshopCategoryFromRequest(request, category);
        return workshopCategoryMapper.toWorkshopCategoryResponse(workshopCategoryRepository.save(category));
    }

    public List<WorkshopCategoryResponse> getAllWorkshopCategories() {
        return workshopCategoryRepository.findAllByActive(true).stream()
                .map(workshopCategoryMapper::toWorkshopCategoryResponse)
                .toList();
    }

    public WorkshopCategoryResponse getWorkshopCategory(Long id) {
        WorkshopCategory category = findWorkshopCategoryByIdAndActive(id);
        return workshopCategoryMapper.toWorkshopCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteWorkshopCategory(Long id) {
        WorkshopCategory category = findWorkshopCategoryByIdAndActive(id);
        category.setActive(false);
        workshopCategoryRepository.save(category);
    }

    private WorkshopCategory findWorkshopCategoryByIdAndActive(Long id) {
        return workshopCategoryRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSHOP_CATEGORY_NOT_FOUND));
    }
}
