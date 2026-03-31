package com.an.api_gateway.configuration;

import com.an.api_gateway.dto.response.ApiResponse;
import com.an.api_gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// chặn request và kiểm tra token trước khi cho đi tiếp sang các service khác.
public class AuthenticationFilter implements GlobalFilter, Ordered {

    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoints = {
            "/identity/auth/.*",
            "/identity/users/registration",
            "/notification/email/send"}; // những endpoint nào không cần xác thực, ví dụ login, logout, register  của service identity thì không cần xác thực token

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    // Mono: sẽ trả về kết quả trong tương lai
    // exchange: nơi chứa request và cả response khi trả về cho client
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter AuthenticationFilter");

        // check xem có nằm trong public endpoint ko
        if (isPublicEndpoint(exchange.getRequest())){
            return chain.filter(exchange); // tiếp tục filter tiếp
        }

        // Get token from authorization header
        List<String>authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if(CollectionUtils.isEmpty(authHeader)){ // nếu không có thì trả 401
            return unauthenticated(exchange.getResponse()); // getResponse để trả về cho client, hàm unauthenticated sẽ trả về response 401 Unauthenticated
        }

        String token = authHeader.getFirst().replace("Bearer ", "");
        log.info("Authentication Token: {}", token);

        // Verify token
            // Delegate to identity service
        // flatMap để xử lý kết quả bên trong Mono
        return identityService.introspect(token).flatMap(introspectResponse ->{   // service->client(cần webclient)-> gọi sang idenServ
            if (introspectResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    // hàm này để xác định thứ tự của filter, nếu có nhiều filter thì filter nào có thứ tự thấp hơn sẽ được thực thi trước
    // set là âm để được chạy đầu tiên
    public int getOrder() {
        return -1;
    }

    // Hàm này để trả về response 401 Unauthenticated nếu không có token trong header
    // exchange.getResponse() → trả response
    Mono<Void> unauthenticated(ServerHttpResponse response) {

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();

        String body = "null";
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private boolean isPublicEndpoint(ServerHttpRequest request){
        return Arrays.stream(publicEndpoints).anyMatch(endpoint -> request.getURI().getPath().matches(apiPrefix + endpoint));
    }
}
