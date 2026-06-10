package com.example.inpe_ingestor.service;

import java.util.Map;

public record ImportacaoResultado(
        int focosPublicados,
        int focosIgnorados,
        Map<String, String> riscosPorBioma
) {
}
