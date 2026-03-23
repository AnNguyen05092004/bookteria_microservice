package com.example.api_gateway.configuration;

import com.example.api_gateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

// Class này là một config để gọi HTTP sang service khác (identity-service) theo kiểu hiện đại của Spring (thay cho Feign/WebClient thuần)
@Configuration
public class WebClientConfiguration {
    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/identity")
                .build();
    }


    @Bean
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
        return factory.createClient(IdentityClient.class);
    }

}
