package com.example.inpe_ingestor.service;

import java.util.Map;

/**
 * Resumo de uma execução de importação.
 *
 * @param focosPublicados focos publicados na fila do RabbitMQ com sucesso
 * @param focosIgnorados  linhas descartadas (ex.: geom inválido / sem coordenada)
 * @param riscosPorBioma  mapa bioma -> idRisco criado nesta execução
 */
public record ImportacaoResultado(
        int focosPublicados,
        int focosIgnorados,
        Map<String, String> riscosPorBioma
) {
}
