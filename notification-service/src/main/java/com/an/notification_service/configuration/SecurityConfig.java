package com.an.notification_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// tham khảo trang spring security architecture

@Configuration // annotation này để đánh dấu đây là một class cấu hình, Spring sẽ tự động phát hiện và xử lý nó khi khởi
// động ứng dụng
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {"/email/send"};

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Cấu hình để cho phép tất cả các yêu cầu POST đến các endpoint được chỉ định trong PUBLIC_ENDPOINTS mà không
        // cần xác thực, trong khi yêu cầu xác thực cho tất cả các yêu cầu khác.
        http.authorizeHttpRequests(requests -> requests.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());

        // Cấu hình để sử dụng JWT làm phương thức xác thực cho các yêu cầu đến server.
        // Khi một yêu cầu đến, Spring Security sẽ kiểm tra header Authorization để tìm token JWT, sau đó sử dụng
        // JwtDecoder để giải mã và xác thực token đó.
        // Nếu token hợp lệ, yêu cầu sẽ được phép truy cập vào tài nguyên được bảo vệ; nếu không, yêu cầu sẽ bị từ chối.
        http.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter())) // để trích xuất thông tin về quyền
                        // (authorities) từ token JWT và ánh xạ
                        // chúng thành các đối tượng
                        // GrantedAuthority mà Spring Security có
                        // thể hiểu được.
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint()) // làm điểm vào xác thực cho các yêu cầu không có
                // token JWT hợp lệ.
                );

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> {}); // ⭐ BẮT BUỘC phải cấu hình CORS nếu frontend và backend chạy trên các domain khác nhau,
        // nếu không sẽ bị lỗi CORS khi
        return http.build();
    }

    @Bean
    // Để customize cách Spring Security trích xuất thông tin về quyền (authorities) từ token JWT
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(
                ""); // Bỏ tiền tố mặc định "SCOPE_" để tự định nghĩa lại trong auhenticationservice

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
