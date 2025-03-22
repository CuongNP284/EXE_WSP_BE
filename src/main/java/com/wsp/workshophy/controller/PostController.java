package com.wsp.workshophy.controller;

import com.wsp.workshophy.dto.request.ApiResponse;
import com.wsp.workshophy.dto.request.PostCreationRequest;
import com.wsp.workshophy.dto.response.PostResponse;
import com.wsp.workshophy.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostController {
    PostService postService;

    @PostMapping("/create")
    ApiResponse<PostResponse> createPost(@RequestBody PostCreationRequest request) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.createPost(request))
                .message("Post created successfully")
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<PostResponse>> getAllApprovedPosts() {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getAllApprovedPosts())
                .message("Approved posts retrieved successfully")
                .build();
    }

    @GetMapping("/{postId}")
    ApiResponse<PostResponse> getPostDetail(@PathVariable Long postId) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.getPostDetail(postId))
                .message("Post detail retrieved successfully")
                .build();
    }

    @GetMapping("/my-history")
    ApiResponse<List<PostResponse>> getMyPostHistory() {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getMyPostHistory())
                .message("Post history retrieved successfully")
                .build();
    }

    @GetMapping("/admin/all")
    ApiResponse<List<PostResponse>> getAllPostsForAdmin() {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getAllPostsForAdmin())
                .message("All posts retrieved successfully")
                .build();
    }

    @GetMapping("/admin/{postId}")
    ApiResponse<PostResponse> getPostForAdmin(@PathVariable Long postId) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.getPostForAdmin(postId))
                .message("Post detail retrieved successfully")
                .build();
    }

    @DeleteMapping("/admin/{postId}")
    ApiResponse<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ApiResponse.<String>builder()
                .result("Post has been deactivated")
                .message("Post deleted successfully")
                .build();
    }

    @PutMapping("/admin/approve/{postId}")
    ApiResponse<PostResponse> approvePost(@PathVariable Long postId) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.approvePost(postId))
                .message("Post approved successfully")
                .build();
    }

    @PutMapping("/admin/reject/{postId}")
    ApiResponse<PostResponse> rejectPost(@PathVariable Long postId) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.rejectPost(postId))
                .message("Post rejected successfully")
                .build();
    }
}
