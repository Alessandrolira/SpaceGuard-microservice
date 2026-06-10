package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.service.TokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor(TokenProvider tokenProvider) {
        return template -> {
            // No momento do interceptor, template.url() é só o path (ex.: "/risco").
            String alvo = template.url();
            if (alvo.contains("/risco")) {
                template.header("Authorization", "Bearer " + tokenProvider.getToken());
            }
        };
    }
}
