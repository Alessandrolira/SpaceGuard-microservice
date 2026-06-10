package com.example.inpe_ingestor.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EstimadorService {

    private static final Map<String, Double> FRP_BASE = Map.of(
            "Amazônia", 55.0,
            "Cerrado", 65.0,
            "Caatinga", 45.0,
            "Mata Atlântica", 35.0,
            "Pantanal", 70.0,
            "Pampa", 30.0
    );

    private static final double FRP_PADRAO = 50.0;

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


    public BigDecimal pontuacaoPorBioma(String bioma) {
        return BigDecimal.valueOf(FRP_BASE.getOrDefault(bioma, FRP_PADRAO));
    }


    public BigDecimal riscoFogoDeConfidence(int confidence) {
        return BigDecimal.valueOf(confidence)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
