package com.example.spaceguard.domain.risco.dto;

import com.example.spaceguard.domain.risco.EnumNivelRisco;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RiscoResponseDTO(

        String idRisco,
        EnumNivelRisco nivelRisco,
        BigDecimal pontuacao

) {
}