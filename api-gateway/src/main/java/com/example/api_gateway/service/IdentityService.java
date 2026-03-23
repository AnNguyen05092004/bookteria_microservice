package com.example.api_gateway.service;

import com.example.api_gateway.dto.request.IntrospectRequest;
import com.example.api_gateway.dto.response.ApiResponse;
import com.example.api_gateway.dto.response.IntrospectResponse;
import com.example.api_gateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {
        return identityClient.introspect(IntrospectRequest.builder().token(token).build());
    }
}
