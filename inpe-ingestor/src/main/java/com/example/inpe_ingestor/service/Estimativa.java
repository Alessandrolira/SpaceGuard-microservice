package com.example.inpe_ingestor.service;

/**
 * Resultado da heurística do EstimadorService.
 * Equivale à tupla (frp, brightness, confidence) do _estimar_frp_brightness do Python.
 */
public record Estimativa(
        double frp,
        double brightness,
        int confidence
) {
}
