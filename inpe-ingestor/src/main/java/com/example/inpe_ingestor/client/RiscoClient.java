package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.dto.RiscoRequest;
import com.example.inpe_ingestor.dto.RiscoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente do CRUD de Risco do spaceguard.
 * <p>
 * Usa o FeignAuthConfig, então toda requisição leva o header
 * Authorization: Bearer &lt;token&gt; (endpoint protegido por JWT).
 */
@FeignClient(
        name = "spaceguard-risco",
        url = "${spaceguard.base-url}"
)
public interface RiscoClient {

    @PostMapping("/risco")
    RiscoResponse criarRisco(@RequestBody RiscoRequest request);
}
