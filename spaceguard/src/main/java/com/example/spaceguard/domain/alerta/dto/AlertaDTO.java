package com.example.spaceguard.domain.alerta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlertaDTO(

        String tituloAlerta,
        String msgAlerta,
        BigDecimal distancia,
        LocalDate dataEmissao,
        LocalDate dataExpir,
        String idUsuario,
        String idFoco

) {
}
