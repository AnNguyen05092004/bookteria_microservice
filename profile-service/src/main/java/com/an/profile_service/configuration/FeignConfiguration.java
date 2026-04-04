package com.an.profile_service.configuration;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// class này để inject SpringFormEncoder vào OpenFeign, giúp OpenFeign có thể xử lý multipart/form-data khi gọi sang service khác (ví dụ: gọi sang file-service để upload file)
@Configuration
public class FeignConfiguration {
    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder();
    }
}
