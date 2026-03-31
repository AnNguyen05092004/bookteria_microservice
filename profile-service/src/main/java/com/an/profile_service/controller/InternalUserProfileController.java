package com.an.profile_service.controller;

import com.an.profile_service.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import com.an.profile_service.dto.request.ProfileCreationRequest;
import com.an.profile_service.dto.response.UserProfileResponse;
import com.an.profile_service.service.UserProfileService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    ApiResponse<UserProfileResponse> createProfile(@RequestBody ProfileCreationRequest profileCreationRequest) {
        return ApiResponse.<UserProfileResponse>builder()
                .message("Profile created successfully")
                .result(userProfileService.createProfile(profileCreationRequest))
                .build();
    }

    @GetMapping("/internal/users/{userId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .message("Profile retrieved successfully")
                .result(userProfileService.getByUserId(userId))
                .build();
    }

}
