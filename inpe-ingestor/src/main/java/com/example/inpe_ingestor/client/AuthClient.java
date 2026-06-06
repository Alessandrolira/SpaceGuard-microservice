package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.dto.LoginRequest;
import com.example.inpe_ingestor.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente do endpoint de autenticação do spaceguard.
 * <p>
 * IMPORTANTE: este client NÃO usa o FeignAuthConfig, ou seja, NÃO manda o header
 * Authorization. O /auth/login é público (permitAll) e não pode receber token —
 * é justamente ele quem gera o token.
 */
@FeignClient(name = "spaceguard-auth", url = "${spaceguard.base-url}")
public interface AuthClient {

    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);
}
