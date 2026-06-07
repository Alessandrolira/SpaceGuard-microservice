package com.example.spaceguard.domain.chat.dto;

/**
 * Resposta do assistente de IA.
 *
 * @param resposta          texto gerado pelo modelo
 * @param focosConsiderados quantos focos do banco entraram no contexto da pergunta
 */
public record ChatResponse(
        String resposta,
        int focosConsiderados
) {
}
