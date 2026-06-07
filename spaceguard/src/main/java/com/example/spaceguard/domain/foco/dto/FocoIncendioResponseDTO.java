package com.example.spaceguard.domain.foco.dto;

import com.example.spaceguard.domain.risco.dto.RiscoDTO;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FocoIncendioResponseDTO(

        String idFoco,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal riscoFogo,
        String bioma,
        String municipio,
        String estado,
        Boolean focoAtivo,
        RiscoDTO risco

) {
}
