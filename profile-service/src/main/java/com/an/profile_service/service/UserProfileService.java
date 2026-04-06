package com.an.profile_service.service;

import java.io.IOException;
import java.util.List;

import com.an.profile_service.dto.request.SearchUserRequest;
import com.an.profile_service.dto.request.UpdateProfileRequest;
import com.an.profile_service.exception.AppException;
import com.an.profile_service.exception.ErrorCode;
import com.an.profile_service.repository.httpclient.FileClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.an.profile_service.dto.request.ProfileCreationRequest;
import com.an.profile_service.dto.response.UserProfileResponse;
import com.an.profile_service.entity.UserProfile;
import com.an.profile_service.mapper.UserProfileMapper;
import com.an.profile_service.repository.UserProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    FileClient fileClient;

    public UserProfileResponse createProfile(ProfileCreationRequest profileCreationRequest) {
        UserProfile userProfile = userProfileMapper.toUserProfile(profileCreationRequest);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile userProfile =
                userProfileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        UserProfileResponse userProfileResponse = userProfileMapper.toUserProfileResponse(userProfile);
        return userProfileResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        List<UserProfileResponse> userProfileResponses = userProfiles.stream()
                .map(userProfileMapper::toUserProfileResponse)
                .toList();
        return userProfileResponses;
    }

    public UserProfileResponse getByUserId(String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        userProfileMapper.update(userProfile, request);
        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    public UserProfileResponse updateAvatar(MultipartFile file) throws IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfile userProfile = userProfileRepository.findByUserId(userId);

        // upload file - call file service
        var response = fileClient.uploadMedia(file); //apiresponse

        // get url and save to user profile
        userProfile.setAvatar(response.getResult().getUrl());
        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    public List<UserProfileResponse> searchUsers(SearchUserRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserProfile> userProfiles = userProfileRepository.findAllByUsernameLike(request.getKeyword());
        return userProfiles.stream()
                .filter(userProfile -> !userProfile.getUserId().equals(userId))
                .map(userProfileMapper::toUserProfileResponse)
                .toList();
    }
}
