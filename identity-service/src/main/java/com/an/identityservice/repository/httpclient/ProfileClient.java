package com.an.identityservice.repository.httpclient;

import com.an.identityservice.configuration.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.an.identityservice.dto.request.ProfileCreationRequest;
import com.an.identityservice.dto.response.UserProfileResponse;

//
@FeignClient(name = "profile-service", url = "${app.services.profile}", configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = "application/json")
    UserProfileResponse createProfile(
            //@RequestHeader("Authorization") String token, // truyền token mới có the create, tuy nhiên fien cũng đã có hỗ trợ. => thêm AuthenticationRequestIntercepter
            @RequestBody ProfileCreationRequest profileCreationRequest);
}
