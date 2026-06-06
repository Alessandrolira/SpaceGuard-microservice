package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.service.TokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Interceptor de autenticação GLOBAL para os Feign clients.
 * <p>
 * Diferente da versão anterior (que era compartilhada via {@code configuration=}
 * entre RiscoClient e FocoClient e acabava sendo aplicada só a um deles), esta é
 * um {@code @Configuration} global: o Spring Cloud aplica o interceptor a TODOS
 * os clients. Para não vazar o token onde não deve, o header só é adicionado nas
 * rotas protegidas do spaceguard (/risco e /foco-incendio).
 * <p>
 * Assim, são deixados de fora de propósito:
 * <ul>
 *   <li>/auth/login (AuthClient) — geraria recursão e o login não leva token;</li>
 *   <li>/geoserver/... (InpeClient) — o INPE é público.</li>
 * </ul>
 */
@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor(TokenProvider tokenProvider) {
        return template -> {
            // No momento do interceptor, template.url() é só o path (ex.: "/foco-incendio").
            String alvo = template.url();
            if (alvo.contains("/risco") || alvo.contains("/foco-incendio")) {
                template.header("Authorization", "Bearer " + tokenProvider.getToken());
            }
        };
    }
}
