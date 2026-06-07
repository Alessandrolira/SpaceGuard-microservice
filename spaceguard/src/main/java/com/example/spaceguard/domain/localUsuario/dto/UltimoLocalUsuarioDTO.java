package com.example.spaceguard.domain.localUsuario.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UltimoLocalUsuarioDTO(

        String idUsuario,
        String nomeUsuario,
        String telefone,
        String email,
        LocaisUsuarioDTO local

) {
}
