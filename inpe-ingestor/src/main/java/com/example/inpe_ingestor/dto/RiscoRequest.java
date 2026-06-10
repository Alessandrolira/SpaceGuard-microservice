package com.example.inpe_ingestor.dto;

import java.math.BigDecimal;

public record RiscoRequest(
        String nivelRisco,
        BigDecimal pontuacao
) {
}
