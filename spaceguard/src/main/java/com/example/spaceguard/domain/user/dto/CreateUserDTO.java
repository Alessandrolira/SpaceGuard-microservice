package com.example.spaceguard.domain.user.dto;

import com.example.spaceguard.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateUserDTO(

        @NotBlank
        String nomeUsuario,

        @NotBlank
        String telefone,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha

) {
}
