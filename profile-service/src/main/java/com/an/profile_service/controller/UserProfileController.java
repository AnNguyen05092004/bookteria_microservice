package com.an.profile_service.controller;

import java.io.IOException;
import java.util.List;

import com.an.profile_service.dto.request.UpdateProfileRequest;
import org.springframework.web.bind.annotation.*;

import com.an.profile_service.dto.response.ApiResponse;
import com.an.profile_service.dto.response.UserProfileResponse;
import com.an.profile_service.service.UserProfileService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/users/my-profile")
    ApiResponse<UserProfileResponse> getMyProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getMyProfile())
                .build();
    }

    @PutMapping("/users/my-profile")
    ApiResponse<UserProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest request){
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateMyProfile(request))
                .build();
    }

    @PutMapping("/users/avatar")
    ApiResponse<UserProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateAvatar(file))
                .build();
    }
}
