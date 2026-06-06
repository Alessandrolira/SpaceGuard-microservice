package com.example.inpe_ingestor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Corpo enviado para POST /foco-incendio do spaceguard.
 * Espelha o FocoIncendioDTO da API.
 * <p>
 * Origem de cada campo no CSV do INPE (camada ams1h:active-fire-today):
 * <ul>
 *   <li>latitude/longitude  -> coluna "geom" (POINT (lon lat))</li>
 *   <li>dataDeteccao        -> coluna "view_date"</li>
 *   <li>bioma               -> coluna "biome"</li>
 *   <li>municipio           -> coluna "municipio"</li>
 *   <li>focoAtivo           -> sempre true (a camada é "active-fire-today")</li>
 *   <li>estado              -> não existe no CSV (vai null)</li>
 *   <li>riscoFogo           -> estimado (confidence / 100)</li>
 *   <li>idRisco             -> Risco criado por bioma via POST /risco</li>
 * </ul>
 */
public record FocoIncendioRequest(
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDate dataDeteccao,
        BigDecimal riscoFogo,
        String bioma,
        String municipio,
        String estado,
        Boolean focoAtivo,
        String idRisco
) {
}
