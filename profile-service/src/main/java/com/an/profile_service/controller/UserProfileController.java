package com.an.profile_service.controller;

import com.an.profile_service.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import com.an.profile_service.dto.request.ProfileCreationRequest;
import com.an.profile_service.dto.response.UserProfileResponse;
import com.an.profile_service.service.UserProfileService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

//    @PostMapping("/users")
//    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest profileCreationRequest) {
//        return userProfileService.createProfile(profileCreationRequest);
//    }

    @GetMapping("/users/{profileId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable("profileId") String profileId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getProfile(profileId))
                .build();
    }

    @GetMapping("/users")
    ApiResponse<List<UserProfileResponse>> getAllProfiles() {
        List<UserProfileResponse> userProfileResponses = userProfileService.getAllProfiles();
        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userProfileResponses)
                .build();
    }
}
