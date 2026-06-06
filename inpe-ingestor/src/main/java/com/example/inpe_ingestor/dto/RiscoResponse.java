package com.example.inpe_ingestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Resposta de POST /risco do spaceguard.
 * <p>
 * O endpoint devolve um EntityModel (HATEOAS), ou seja, além dos campos vem um
 * objeto "_links". O {@code @JsonIgnoreProperties(ignoreUnknown = true)} faz o
 * Feign ignorar esse "_links" e desserializar só o que interessa — sobretudo o
 * idRisco, que é o que vamos usar como FK no foco.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiscoResponse(
        String idRisco,
        String nivelRisco,
        BigDecimal pontuacao
) {
}
