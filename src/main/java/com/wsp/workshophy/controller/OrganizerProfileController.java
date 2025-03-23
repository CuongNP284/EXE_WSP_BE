package com.wsp.workshophy.controller;

import com.wsp.workshophy.dto.request.ApiResponse;
import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileCreationRequest;
import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileUpdateRequest;
import com.wsp.workshophy.dto.response.OrganizerProfileResponse;
import com.wsp.workshophy.service.OrganizerProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizer-profiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrganizerProfileController {
    OrganizerProfileService organizerProfileService;

    @PostMapping("/create")
    public ApiResponse<OrganizerProfileResponse> createOrganizerProfile(@RequestBody OrganizerProfileCreationRequest request) {
        OrganizerProfileResponse result = organizerProfileService.createOrganizerProfile(request);
        return ApiResponse.<OrganizerProfileResponse>builder()
                .result(result)
                .message("Organizer profile created successfully")
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<OrganizerProfileResponse> updateOrganizerProfile(@PathVariable Long id, @RequestBody OrganizerProfileUpdateRequest request) {
        OrganizerProfileResponse result = organizerProfileService.updateOrganizerProfile(id, request);
        return ApiResponse.<OrganizerProfileResponse>builder()
                .result(result)
                .message("Organizer profile updated successfully")
                .build();
    }

    @GetMapping("/getAll")
    public ApiResponse<List<OrganizerProfileResponse>> getAllOrganizerProfiles() {
        List<OrganizerProfileResponse> result = organizerProfileService.getAllOrganizerProfiles();
        return ApiResponse.<List<OrganizerProfileResponse>>builder()
                .result(result)
                .message("Retrieved all active organizer profiles successfully")
                .build();
    }

    @GetMapping("/getOneByOrganizer/{id}")
    public ApiResponse<OrganizerProfileResponse> getOrganizerProfileByOrganizer(@PathVariable Long id) {
        OrganizerProfileResponse result = organizerProfileService.getOrganizerProfile(id);
        return ApiResponse.<OrganizerProfileResponse>builder()
                .result(result)
                .message("Organizer profile retrieved successfully")
                .build();
    }

    @GetMapping("/getOneByCustomer/{id}")
    public ApiResponse<OrganizerProfileResponse> getOrganizerProfileByCustomer(@PathVariable Long id) {
        OrganizerProfileResponse result = organizerProfileService.getOrganizerProfileForUser(id);
        return ApiResponse.<OrganizerProfileResponse>builder()
                .result(result)
                .message("Organizer profile retrieved successfully")
                .build();
    }

    @GetMapping("/getOneByUserId/{userId}")
    public ApiResponse<OrganizerProfileResponse> getOrganizerProfileByUserId(@PathVariable String userId) {
        OrganizerProfileResponse result = organizerProfileService.getOrganizerProfileByUserId(userId);
        return ApiResponse.<OrganizerProfileResponse>builder()
                .result(result)
                .message("Organizer profile retrieved successfully")
                .build();
    }

    @GetMapping("/searchByName")
    public ApiResponse<List<OrganizerProfileResponse>> searchOrganizerProfilesByName(@RequestParam String name) {
        List<OrganizerProfileResponse> result = organizerProfileService.searchOrganizerProfilesByName(name);
        return ApiResponse.<List<OrganizerProfileResponse>>builder()
                .result(result)
                .message("Organizer profiles retrieved successfully")
                .build();
    }

    @GetMapping("/my-profile")
    public ApiResponse<OrganizerProfileResponse> getMyOrganizerProfile() {
        OrganizerProfileResponse result = organizerProfileService.getMyOrganizerProfile();
        return ApiResponse.<OrganizerProfileResponse>builder()
                .result(result)
                .message("Retrieved my organizer profile successfully")
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteOrganizerProfile(@PathVariable Long id) {
        organizerProfileService.deleteOrganizerProfile(id);
        return ApiResponse.<String>builder()
                .result("Organizer profile has been deactivated")
                .message("Organizer profile deactivated successfully")
                .build();
    }
}
