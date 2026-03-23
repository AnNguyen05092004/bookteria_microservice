package com.an.profile_service.mapper;

import org.mapstruct.Mapper;

import com.an.profile_service.dto.request.ProfileCreationRequest;
import com.an.profile_service.dto.response.UserProfileResponse;
import com.an.profile_service.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest profileCreationRequest);

    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
}
