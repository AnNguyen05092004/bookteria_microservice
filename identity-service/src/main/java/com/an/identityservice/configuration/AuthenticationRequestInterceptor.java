package com.an.identityservice.configuration;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component (Global) // khi khai báo bean, openFeign sẽ tự động tìm kiếm tất cả các bean implement interface
// RequestInterceptor và sử dụng chúng để thêm header vào request khi gọi sang service khác
// ==> Nên config cho từng client
public class AuthenticationRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        var authHeader = requestAttributes.getRequest().getHeader("Authorization");
        log.info("authHeader: {}", authHeader);

        if (authHeader != null) {
            requestTemplate.header(
                    "Authorization", authHeader); // thêm header Authorization vào request khi gọi sang service khác
        }
    }
}
