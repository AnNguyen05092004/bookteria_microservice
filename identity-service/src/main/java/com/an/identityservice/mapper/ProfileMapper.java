package com.an.identityservice.mapper;

import org.mapstruct.Mapper;

import com.an.identityservice.dto.request.ProfileCreationRequest;
import com.an.identityservice.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest userCreationRequest);
}
