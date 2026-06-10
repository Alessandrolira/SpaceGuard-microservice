package com.example.inpe_ingestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RiscoResponse(
        String idRisco,
        String nivelRisco,
        BigDecimal pontuacao
) {
}
