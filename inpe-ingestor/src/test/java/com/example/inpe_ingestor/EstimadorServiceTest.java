package com.example.inpe_ingestor;

import com.example.inpe_ingestor.service.EstimadorService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios da heuristica de estimativa (EstimadorService).
 * Sao deterministicos (nao usam a parte aleatoria), entao rodam de forma
 * confiavel no CI e produzem resultados JUnit para a pipeline publicar.
 */
class EstimadorServiceTest {

    private final EstimadorService estimador = new EstimadorService();

    @Test
    void nivelPorBiomaDeveSeguirRanking() {
        assertEquals("ALTO", estimador.nivelPorBioma("Pantanal"));
        assertEquals("ALTO", estimador.nivelPorBioma("Cerrado"));
        assertEquals("MEDIO", estimador.nivelPorBioma("Amazônia"));
        assertEquals("MEDIO", estimador.nivelPorBioma("Caatinga"));
        assertEquals("BAIXO", estimador.nivelPorBioma("Pampa"));
    }

    @Test
    void pontuacaoPorBiomaUsaPesoBase() {
        assertEquals(0, estimador.pontuacaoPorBioma("Cerrado")
                .compareTo(BigDecimal.valueOf(65.0)));
        // bioma desconhecido cai no padrao (50.0)
        assertEquals(0, estimador.pontuacaoPorBioma("Inexistente")
                .compareTo(BigDecimal.valueOf(50.0)));
    }

    @Test
    void riscoFogoDeConfidenceConverteParaFaixa0a1() {
        assertEquals(0, estimador.riscoFogoDeConfidence(80)
                .compareTo(new BigDecimal("0.80")));
    }
}
