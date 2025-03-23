package com.wsp.workshophy.controller;

import java.util.List;

import com.wsp.workshophy.dto.response.FollowResponse;
import com.wsp.workshophy.dto.response.FollowerUsernameResponse;
import com.wsp.workshophy.dto.response.OrganizerProfileNameResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.wsp.workshophy.dto.request.ApiResponse;
import com.wsp.workshophy.dto.request.User.UserCreationRequest;
import com.wsp.workshophy.dto.request.User.UserUpdateRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/createUser")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .message("User created successfully")
                .build();
    }

    @GetMapping("/getUsers")
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .message("Users retrieved successfully")
                .build();
    }

    @GetMapping("/organizers/matching-categories")
    public ApiResponse<List<UserResponse>> findOrganizersByMatchingCategories() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.findOrganizersByMatchingCategories())
                .message("Matching organizers retrieved successfully")
                .build();
    }

    @GetMapping("/getUser/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .message("User retrieved successfully")
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .message("My info retrieved successfully")
                .build();
    }

    @DeleteMapping("/delete/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .message("User deleted successfully")
                .build();
    }

    @PutMapping("/updateUser/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("User updated successfully")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @PostMapping("/{userId}/follow")
    public ApiResponse<FollowResponse> followUser(@PathVariable String userId) {
        FollowResponse result = userService.followUser(userId);
        return ApiResponse.<FollowResponse>builder()
                .result(result)
                .message(result.getMessage())
                .build();
    }

    @PostMapping("/{userId}/unfollow")
    public ApiResponse<FollowResponse> unfollowUser(@PathVariable String userId) {
        FollowResponse result = userService.unfollowUser(userId);
        return ApiResponse.<FollowResponse>builder()
                .result(result)
                .message(result.getMessage())
                .build();
    }

    @GetMapping("/my-followed-organizers")
    //For Customer
    public ApiResponse<List<OrganizerProfileNameResponse>> getFollowedOrganizerProfiles() {
        List<OrganizerProfileNameResponse> result = userService.getFollowedOrganizerProfiles();
        return ApiResponse.<List<OrganizerProfileNameResponse>>builder()
                .result(result)
                .message("Followed organizer profiles retrieved successfully")
                .build();
    }

    //For Organizer
    @GetMapping("/my-followers")
    public ApiResponse<List<FollowerUsernameResponse>> getFollowersForOrganizerProfile() {
        List<FollowerUsernameResponse> result = userService.getFollowersForOrganizerProfile();
        return ApiResponse.<List<FollowerUsernameResponse>>builder()
                .result(result)
                .message("Followers retrieved successfully")
                .build();
    }

    @GetMapping("/searchUserByName")
    public ApiResponse<List<UserResponse>> searchUsersByUsername(@RequestParam String username) {
        List<UserResponse> result = userService.searchUsersByUsername(username);
        return ApiResponse.<List<UserResponse>>builder()
                .result(result)
                .message("Users retrieved successfully")
                .build();
    }
}
