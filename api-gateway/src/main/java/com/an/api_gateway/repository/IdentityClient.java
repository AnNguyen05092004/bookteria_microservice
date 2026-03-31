package com.an.api_gateway.repository;

import com.an.api_gateway.dto.request.IntrospectRequest;
import com.an.api_gateway.dto.response.ApiResponse;
import com.an.api_gateway.dto.response.IntrospectResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@Component
public interface IdentityClient {
    @PostExchange(url ="/auth/introspect", contentType = "application/json")
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
