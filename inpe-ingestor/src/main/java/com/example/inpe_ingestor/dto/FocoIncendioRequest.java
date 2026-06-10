package com.example.inpe_ingestor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FocoIncendioRequest(
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDate dataDeteccao,
        BigDecimal riscoFogo,
        String bioma,
        String municipio,
        String estado,
        Boolean focoAtivo,
        String idRisco
) {
}
