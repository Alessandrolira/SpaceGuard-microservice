package com.example.inpe_ingestor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente do WFS/GeoServer do TerraBrasilis (INPE).
 * <p>
 * Baixa a camada de focos ativos do dia em CSV. NÃO usa o FeignAuthConfig —
 * o INPE é público e não recebe o token do spaceguard.
 * <p>
 * URL equivalente (a mesma que o amigo usa no Python):
 * {@code https://terrabrasilis.dpi.inpe.br/geoserver/ams1h/ows?service=WFS
 * &version=1.0.0&request=GetFeature&typeName=ams1h:active-fire-today
 * &outputFormat=csv&maxFeatures=500}
 */
@FeignClient(name = "inpe", url = "${inpe.base-url}")
public interface InpeClient {

    @GetMapping(value = "/geoserver/ams1h/ows", produces = "text/csv")
    String baixarFocosAtivos(
            @RequestParam("service") String service,
            @RequestParam("version") String version,
            @RequestParam("request") String request,
            @RequestParam("typeName") String typeName,
            @RequestParam("outputFormat") String outputFormat,
            @RequestParam("maxFeatures") int maxFeatures,
            @RequestParam("sortBy") String sortBy
    );
}
