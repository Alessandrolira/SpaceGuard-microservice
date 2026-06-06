package com.example.inpe_ingestor.dto;

/**
 * Corpo enviado para POST /auth/login do spaceguard.
 * Espelha o LoginRequestDTO da API (email + senha).
 */
public record LoginRequest(
        String email,
        String senha
) {
}
