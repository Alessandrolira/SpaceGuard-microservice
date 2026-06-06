package com.example.inpe_ingestor.service;

import java.util.Map;

/**
 * Resumo de uma execução de importação.
 *
 * @param focosEnviados   focos inseridos com sucesso no spaceguard
 * @param focosIgnorados  linhas descartadas (ex.: geom inválido / sem coordenada)
 * @param riscosPorBioma  mapa bioma -> idRisco criado nesta execução
 */
public record ImportacaoResultado(
        int focosEnviados,
        int focosIgnorados,
        Map<String, String> riscosPorBioma
) {
}
