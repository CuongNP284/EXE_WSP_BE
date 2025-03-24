package com.wsp.workshophy.controller;

import com.wsp.workshophy.dto.request.AdvertisementRequest;
import com.wsp.workshophy.dto.request.ApiResponse;
import com.wsp.workshophy.dto.response.AdvertisementResponse;
import com.wsp.workshophy.service.AdvertisementService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdvertisementController {
    AdvertisementService advertisementService;

    // ORGANIZER: Tạo quảng cáo
    @PostMapping("/create")
    public ApiResponse<AdvertisementResponse> createAdvertisement(@RequestBody AdvertisementRequest request) {
        AdvertisementResponse result = advertisementService.createAdvertisement(request);
        return ApiResponse.<AdvertisementResponse>builder()
                .result(result)
                .message("Advertisement created successfully")
                .build();
    }

    // ADMIN: Duyệt quảng cáo
    @PutMapping("/{id}/status")
    public ApiResponse<AdvertisementResponse> updateAdvertisementStatus(
            @PathVariable Long id, @RequestParam String status) {
        AdvertisementResponse result = advertisementService.updateAdvertisementStatus(id, status);
        return ApiResponse.<AdvertisementResponse>builder()
                .result(result)
                .message("Advertisement status updated successfully")
                .build();
    }

    // CUSTOMER: Lấy tất cả quảng cáo
    @GetMapping("/getAllByCustomer")
    public ApiResponse<List<AdvertisementResponse>> getAllAdvertisementsForCustomer() {
        List<AdvertisementResponse> result = advertisementService.getAllAdvertisementsForCustomer();
        return ApiResponse.<List<AdvertisementResponse>>builder()
                .result(result)
                .message("Advertisements retrieved successfully")
                .build();
    }

    // CUSTOMER: Lấy một quảng cáo
    @GetMapping("/getOneByCustomer/{id}")
    public ApiResponse<AdvertisementResponse> getAdvertisementForCustomer(@PathVariable Long id) {
        AdvertisementResponse result = advertisementService.getAdvertisementForCustomer(id);
        return ApiResponse.<AdvertisementResponse>builder()
                .result(result)
                .message("Advertisement retrieved successfully")
                .build();
    }

    // ADMIN: Lấy tất cả quảng cáo
    @GetMapping("/getAllByAdmin")
    public ApiResponse<List<AdvertisementResponse>> getAllAdvertisementsForAdmin() {
        List<AdvertisementResponse> result = advertisementService.getAllAdvertisementsForAdmin();
        return ApiResponse.<List<AdvertisementResponse>>builder()
                .result(result)
                .message("Advertisements retrieved successfully")
                .build();
    }

    // ADMIN: Lấy một quảng cáo
    @GetMapping("/getOneByAdmin/{id}")
    public ApiResponse<AdvertisementResponse> getAdvertisementForAdmin(@PathVariable Long id) {
        AdvertisementResponse result = advertisementService.getAdvertisementForAdmin(id);
        return ApiResponse.<AdvertisementResponse>builder()
                .result(result)
                .message("Advertisement retrieved successfully")
                .build();
    }

    // ORGANIZER: Lấy tất cả quảng cáo của mình
    @GetMapping("/getAllByOrganizer")
    public ApiResponse<List<AdvertisementResponse>> getAllAdvertisementsForOrganizer() {
        List<AdvertisementResponse> result = advertisementService.getAllAdvertisementsForOrganizer();
        return ApiResponse.<List<AdvertisementResponse>>builder()
                .result(result)
                .message("Advertisements retrieved successfully")
                .build();
    }

    // ORGANIZER: Lấy một quảng cáo của mình
    @GetMapping("/getOneByOrganizer/{id}")
    public ApiResponse<AdvertisementResponse> getAdvertisementForOrganizer(@PathVariable Long id) {
        AdvertisementResponse result = advertisementService.getAdvertisementForOrganizer(id);
        return ApiResponse.<AdvertisementResponse>builder()
                .result(result)
                .message("Advertisement retrieved successfully")
                .build();
    }

    // ORGANIZER: Xóa quảng cáo
    @DeleteMapping("/deleteByOrganizer/{id}")
    public ApiResponse<String> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);
        return ApiResponse.<String>builder()
                .result("Advertisement has been deleted")
                .message("Advertisement deleted successfully")
                .build();
    }
}
