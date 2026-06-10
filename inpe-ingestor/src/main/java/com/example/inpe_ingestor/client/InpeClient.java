package com.example.inpe_ingestor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
