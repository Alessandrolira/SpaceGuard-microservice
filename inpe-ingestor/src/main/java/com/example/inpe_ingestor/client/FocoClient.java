package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.dto.FocoIncendioRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente do CRUD de Foco de Incêndio do spaceguard.
 * <p>
 * Usa o FeignAuthConfig (endpoint protegido por JWT).
 * <p>
 * O POST devolve um EntityModel (HATEOAS), mas aqui não precisamos do corpo:
 * o retorno {@code void} faz o Feign ignorar a resposta. Se o status não for 2xx,
 * o Feign lança FeignException — então erro de inserção não passa silencioso.
 */
@FeignClient(
        name = "spaceguard-foco",
        url = "${spaceguard.base-url}"
)
public interface FocoClient {

    @PostMapping("/foco-incendio")
    void criarFoco(@RequestBody FocoIncendioRequest request);
}
