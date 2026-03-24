package com.an.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.an.identityservice.dto.request.ProfileCreationRequest;
import com.an.identityservice.dto.response.UserProfileResponse;

//
@FeignClient(name = "profile-service", url = "${app.services.profile}")
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = "application/json")
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest profileCreationRequest);
}
