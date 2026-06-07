package com.example.spaceguard.domain.user.dto;

import com.example.spaceguard.domain.user.UserRole;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UsuarioDTO(

        String idUsuario,
        String nomeUsuario,
        String telefone,
        String email,
        LocalDate dataCadastro,
        UserRole role

) {
}
