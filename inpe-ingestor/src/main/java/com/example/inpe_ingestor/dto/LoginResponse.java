package com.example.inpe_ingestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Resposta de POST /auth/login do spaceguard.
 * A API devolve apenas { "token": "..." }.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponse(
        String token
) {
}
