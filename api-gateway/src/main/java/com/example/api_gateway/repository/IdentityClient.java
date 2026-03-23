package com.example.api_gateway.repository;

import com.example.api_gateway.dto.request.IntrospectRequest;
import com.example.api_gateway.dto.response.ApiResponse;
import com.example.api_gateway.dto.response.IntrospectResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@Component
public interface IdentityClient {
    @PostExchange(url ="/auth/introspect", contentType = "application/json")
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
