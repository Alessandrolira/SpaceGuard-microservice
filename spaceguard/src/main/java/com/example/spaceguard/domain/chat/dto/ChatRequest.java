package com.example.spaceguard.domain.chat.dto;

/**
 * Corpo do POST /chat — a pergunta do usuário para o assistente de IA.
 */
public record ChatRequest(
        String message
) {
}
