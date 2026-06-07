package com.example.spaceguard.domain.foco.dto;

import com.example.spaceguard.domain.risco.Risco;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record FocoIncendioDTO(

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
