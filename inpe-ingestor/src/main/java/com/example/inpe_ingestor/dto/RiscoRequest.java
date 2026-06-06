package com.example.inpe_ingestor.dto;

import java.math.BigDecimal;

/**
 * Corpo enviado para POST /risco do spaceguard.
 * <p>
 * nivelRisco vai como String ("ALTO" | "MEDIO" | "BAIXO"); o Jackson do
 * spaceguard converte automaticamente para o enum EnumNivelRisco.
 */
public record RiscoRequest(
        String nivelRisco,
        BigDecimal pontuacao
) {
}
