package com.an.post.controller;

import com.an.post.dto.ApiResponse;
import com.an.post.dto.request.PostRequest;
import com.an.post.dto.response.PostResponse;
import com.an.post.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping("/create")
    ApiResponse<PostResponse> createPost(@RequestBody PostRequest postRequest) {
        return ApiResponse.<PostResponse>builder()
                .message("Post created successfully")
                .result(postService.createPost(postRequest))
                .build();
    }

    @GetMapping("/my-posts")
    ApiResponse<List<PostResponse>> getMyPosts() {
        return ApiResponse.<List<PostResponse>>builder()
                .message("Get my posts successfully")
                .result(postService.getMyPosts())
                .build();
    }
}