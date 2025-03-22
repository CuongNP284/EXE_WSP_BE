package com.wsp.workshophy.controller;


import com.wsp.workshophy.dto.request.ApiResponse;
import com.wsp.workshophy.dto.request.WorkshopCategory.WorkshopCategoryCreationRequest;
import com.wsp.workshophy.dto.request.WorkshopCategory.WorkshopCategoryUpdateRequest;
import com.wsp.workshophy.dto.response.WorkshopCategoryResponse;
import com.wsp.workshophy.service.WorkshopCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workshop-categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkshopCategoryController {
    WorkshopCategoryService workshopCategoryService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<WorkshopCategoryResponse> createWorkshopCategory(@RequestBody WorkshopCategoryCreationRequest request) {
        WorkshopCategoryResponse result = workshopCategoryService.createWorkshopCategory(request);
        return ApiResponse.<WorkshopCategoryResponse>builder()
                .result(result)
                .message("Workshop category created successfully")
                .build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<WorkshopCategoryResponse> updateWorkshopCategory(@PathVariable Long id, @RequestBody WorkshopCategoryUpdateRequest request) {
        WorkshopCategoryResponse result = workshopCategoryService.updateWorkshopCategory(id, request);
        return ApiResponse.<WorkshopCategoryResponse>builder()
                .result(result)
                .message("Workshop category updated successfully")
                .build();
    }

    @GetMapping("/getAll")
    public ApiResponse<List<WorkshopCategoryResponse>> getAllWorkshopCategories() {
        List<WorkshopCategoryResponse> result = workshopCategoryService.getAllWorkshopCategories();
        return ApiResponse.<List<WorkshopCategoryResponse>>builder()
                .result(result)
                .message("Retrieved all active workshop categories successfully")
                .build();
    }

    @GetMapping("/getOne/{id}")
    public ApiResponse<WorkshopCategoryResponse> getWorkshopCategory(@PathVariable Long id) {
        WorkshopCategoryResponse result = workshopCategoryService.getWorkshopCategory(id);
        return ApiResponse.<WorkshopCategoryResponse>builder()
                .result(result)
                .message("Workshop category retrieved successfully")
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteWorkshopCategory(@PathVariable Long id) {
        workshopCategoryService.deleteWorkshopCategory(id);
        return ApiResponse.<String>builder()
                .result("Workshop category has been deactivated")
                .message("Workshop category deactivated successfully")
                .build();
    }
}
