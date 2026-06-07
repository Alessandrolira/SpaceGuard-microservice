package com.example.spaceguard.domain.localUsuario.dto;

import java.math.BigDecimal;

public record AdicionarLocalDTO(

        String idUsuario,
        BigDecimal latitude,
        BigDecimal longitude

) {
}
