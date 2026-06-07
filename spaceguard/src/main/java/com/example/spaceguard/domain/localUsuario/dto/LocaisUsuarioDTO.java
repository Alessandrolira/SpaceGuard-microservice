package com.example.spaceguard.domain.localUsuario.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record LocaisUsuarioDTO(

        String idUsuario,
        String telefone,
        String email,
        List<LocalUsuarioDTO> listaLocaisUsuario

) {
}
