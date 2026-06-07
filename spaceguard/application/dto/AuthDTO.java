package com.example.spaceguard.application.dto;

public record LoginRequest(
    String email,
    String senha
) {}

public record LoginResponse(
    String token,
    String email
) {}

public record ErrorResponse(
    String message
) {}
