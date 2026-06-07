package com.example.spaceguard.domain.user.dto;

import com.example.spaceguard.domain.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModifyUserDTO(

        @NotBlank
        String nomeUsuario,

        @NotBlank
        String telefone,

        @NotBlank
        String email,

        @NotBlank
        String senha,

        @NotNull
        UserRole role

) {
}
