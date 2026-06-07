package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.service.TokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Interceptor de autenticação GLOBAL para os Feign clients.
 * <p>
 * É um {@code @Configuration} global: o Spring Cloud aplica o interceptor a TODOS
 * os clients. Para não vazar o token onde não deve, o header Authorization só é
 * adicionado na rota protegida do spaceguard usada via Feign (/risco).
 * <p>
 * Assim, são deixados de fora de propósito:
 * <ul>
 *   <li>/auth/login (AuthClient) — geraria recursão e o login não leva token;</li>
 *   <li>/geoserver/... (InpeClient) — o INPE é público.</li>
 * </ul>
 * (O envio de focos não passa mais por Feign — agora vai pela fila do RabbitMQ.)
 */
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
