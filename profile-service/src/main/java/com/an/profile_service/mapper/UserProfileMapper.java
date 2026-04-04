package com.an.profile_service.mapper;

import com.an.profile_service.dto.request.UpdateProfileRequest;
import org.mapstruct.Mapper;

import com.an.profile_service.dto.request.ProfileCreationRequest;
import com.an.profile_service.dto.response.UserProfileResponse;
import com.an.profile_service.entity.UserProfile;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest profileCreationRequest);

    UserProfileResponse toUserProfileResponse(UserProfile userProfile);

    void update(@MappingTarget UserProfile userProfile, UpdateProfileRequest updateProfileRequest);

}
