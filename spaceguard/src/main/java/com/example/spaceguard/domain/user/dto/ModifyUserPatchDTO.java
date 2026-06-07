package com.example.spaceguard.domain.user.dto;

import com.example.spaceguard.domain.user.UserRole;

public record ModifyUserPatchDTO(

        String nomeUsuario,
        String telefone,
        String email,
        String senha,
        UserRole role

) {
}
