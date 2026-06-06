package com.example.inpe_ingestor.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Traduz para Java a heurística do amigo (api.py: _estimar_frp_brightness).
 * <p>
 * O CSV do INPE (camada active-fire-today) NÃO traz frp/brightness/confidence
 * nem nível de risco. O Python estima esses valores a partir do bioma e do mês
 * (sazonalidade da estação seca). Replicamos a mesma lógica aqui.
 * <p>
 * A parte de Machine Learning (RandomForest .pkl) que o amigo usa para o nível
 * de risco NÃO é portável para Java. Como a estratégia escolhida é "Risco por
 * bioma", derivamos o nível diretamente da tabela FRP_BASE — cada bioma já tem
 * um peso natural ali.
 */
@Service
public class EstimadorService {

    /** Peso base de FRP por bioma (api.py:87). Também serve de ranking de risco. */
    private static final Map<String, Double> FRP_BASE = Map.of(
            "Amazônia", 55.0,
            "Cerrado", 65.0,
            "Caatinga", 45.0,
            "Mata Atlântica", 35.0,
            "Pantanal", 70.0,
            "Pampa", 30.0
    );

    private static final double FRP_PADRAO = 50.0;

    /**
     * Estima frp, brightness e confidence (api.py: _estimar_frp_brightness).
     *
     * @param bioma bioma do foco (ex.: "Amazônia")
     * @param mes   mês 1..12 extraído do "viewed_at" do CSV
     */
    public Estimativa estimar(String bioma, int mes) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        double frpMean = FRP_BASE.getOrDefault(bioma, FRP_PADRAO);
        if (mes >= 7 && mes <= 10) {
            frpMean *= 1.8;            // estação seca eleva o FRP
        } else if (mes == 6 || mes == 11) {
            frpMean *= 1.2;
        }

        // Python: random.expovariate(1.0 / frp_mean) -> distribuição exponencial com média frp_mean
        double frp = Math.max(0.5, -frpMean * Math.log(1 - rnd.nextDouble()));
        frp = round1(frp);

        // Python: random.gauss(335, 45), limitado entre 280 e 550
        double brightness = rnd.nextGaussian() * 45 + 335;
        brightness = round1(Math.max(280.0, Math.min(550.0, brightness)));

        // Python: random.randint(50, 95)  (inclusivo nas duas pontas)
        int confidence = rnd.nextInt(50, 96);

        return new Estimativa(frp, brightness, confidence);
    }

    /**
     * Deriva o nível de risco do bioma a partir do peso em FRP_BASE.
     * Pantanal/Cerrado (>=60) -> ALTO; Amazônia/Caatinga (>=40) -> MEDIO; resto -> BAIXO.
     *
     * @return "ALTO" | "MEDIO" | "BAIXO" (string compatível com o enum do spaceguard)
     */
    public String nivelPorBioma(String bioma) {
        double base = FRP_BASE.getOrDefault(bioma, FRP_PADRAO);
        if (base >= 60) {
            return "ALTO";
        }
        if (base >= 40) {
            return "MEDIO";
        }
        return "BAIXO";
    }

    /**
     * Pontuação do Risco daquele bioma = o próprio peso base em FRP.
     * Usado no corpo do POST /risco.
     */
    public BigDecimal pontuacaoPorBioma(String bioma) {
        return BigDecimal.valueOf(FRP_BASE.getOrDefault(bioma, FRP_PADRAO));
    }

    /**
     * riscoFogo do foco no formato 0..1 (igual ao risco_fogo original do INPE),
     * derivado do confidence estimado (50..95 -> 0.50..0.95).
     */
    public BigDecimal riscoFogoDeConfidence(int confidence) {
        return BigDecimal.valueOf(confidence)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
