package com.example.inpe_ingestor.service;

import com.example.inpe_ingestor.client.InpeClient;
import com.example.inpe_ingestor.client.RiscoClient;
import com.example.inpe_ingestor.config.RabbitConfig;
import com.example.inpe_ingestor.dto.FocoIncendioRequest;
import com.example.inpe_ingestor.dto.RiscoRequest;
import com.example.inpe_ingestor.dto.RiscoResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Orquestra a importação ponta a ponta:
 * <ol>
 *   <li>baixa o CSV do INPE (InpeClient);</li>
 *   <li>parseia cada linha (commons-csv) extraindo geom, data, bioma, município;</li>
 *   <li>estima frp/brightness/confidence (EstimadorService);</li>
 *   <li>cria um Risco por bioma no spaceguard (RiscoClient) e guarda o idRisco;</li>
 *   <li>publica cada foco na fila do RabbitMQ (o spaceguard consome e grava).</li>
 * </ol>
 */
@Service
public class ImportacaoService {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoService.class);

    private final InpeClient inpeClient;
    private final RiscoClient riscoClient;
    private final RabbitTemplate rabbitTemplate;
    private final EstimadorService estimador;

    private final String typeName;
    private final int maxFeatures;

    public ImportacaoService(InpeClient inpeClient,
                             RiscoClient riscoClient,
                             RabbitTemplate rabbitTemplate,
                             EstimadorService estimador,
                             @Value("${inpe.type-name}") String typeName,
                             @Value("${inpe.max-features}") int maxFeatures) {
        this.inpeClient = inpeClient;
        this.riscoClient = riscoClient;
        this.rabbitTemplate = rabbitTemplate;
        this.estimador = estimador;
        this.typeName = typeName;
        this.maxFeatures = maxFeatures;
    }

    public ImportacaoResultado importar() {
        log.info("Baixando focos do INPE (typeName={}, maxFeatures={})", typeName, maxFeatures);
        // sortBy "viewed_at D" = ordena por horário de detecção DECRESCENTE,
        // então maxFeatures pega os N focos MAIS RECENTES do dia.
        String csv = inpeClient.baixarFocosAtivos(
                "WFS", "1.0.0", "GetFeature", typeName, "csv", maxFeatures, "viewed_at D");

        log.info("CSV recebido do INPE: {} caracteres", csv == null ? 0 : csv.length());
        if (csv == null || csv.isBlank()) {
            throw new IllegalStateException(
                    "INPE retornou resposta vazia (servidor instável no momento). Tente novamente em alguns segundos.");
        }

        Map<String, String> riscoPorBioma = new HashMap<>();
        int publicados = 0;
        int ignorados = 0;

        CSVFormat formato = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        try (CSVParser parser = formato.parse(new StringReader(csv))) {
            for (CSVRecord registro : parser) {
                BigDecimal[] lonLat = parseGeom(valor(registro, "geom", null));
                if (lonLat == null) {
                    ignorados++;
                    continue;
                }

                String bioma = valor(registro, "biome", "Cerrado");
                String municipio = valor(registro, "municipio", null);
                LocalDate dataDeteccao = parseData(registro);
                int mes = parseMes(registro, dataDeteccao);

                Estimativa est = estimador.estimar(bioma, mes);

                // Cria o Risco do bioma só uma vez por execução e reaproveita o id.
                String idRisco = riscoPorBioma.computeIfAbsent(bioma, this::criarRiscoDoBioma);

                FocoIncendioRequest foco = new FocoIncendioRequest(
                        lonLat[1],                                    // latitude  (2º número do POINT)
                        lonLat[0],                                    // longitude (1º número do POINT)
                        dataDeteccao,
                        estimador.riscoFogoDeConfidence(est.confidence()),
                        bioma,
                        municipio,
                        null,                                         // estado não existe no CSV
                        Boolean.TRUE,                                 // camada active-fire-today
                        idRisco
                );

                // Publica na fila em vez de chamar o spaceguard direto: responde rápido
                // e o spaceguard grava no banco no ritmo dele (consumer).
                rabbitTemplate.convertAndSend(RabbitConfig.FILA_FOCOS, foco);
                publicados++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao parsear o CSV do INPE", e);
        }

        log.info("Importação concluída: {} focos publicados na fila, {} ignorados, {} riscos criados",
                publicados, ignorados, riscoPorBioma.size());
        return new ImportacaoResultado(publicados, ignorados, riscoPorBioma);
    }

    /** Cria no spaceguard um Risco com nível/pontuação derivados do bioma e devolve o idRisco. */
    private String criarRiscoDoBioma(String bioma) {
        RiscoResponse resposta = riscoClient.criarRisco(new RiscoRequest(
                estimador.nivelPorBioma(bioma),
                estimador.pontuacaoPorBioma(bioma)
        ));
        log.info("Risco criado para bioma '{}': id={} nivel={}", bioma, resposta.idRisco(), resposta.nivelRisco());
        return resposta.idRisco();
    }

    /** Extrai [longitude, latitude] de uma string WKT "POINT (lon lat)". Retorna null se inválida. */
    private static BigDecimal[] parseGeom(String geom) {
        if (geom == null || geom.isBlank()) {
            return null;
        }
        try {
            String numeros = geom.replace("POINT", "")
                    .replace("(", "")
                    .replace(")", "")
                    .trim();
            String[] partes = numeros.split("\\s+");
            if (partes.length < 2) {
                return null;
            }
            BigDecimal lon = new BigDecimal(partes[0]);
            BigDecimal lat = new BigDecimal(partes[1]);
            return new BigDecimal[]{lon, lat};
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** dataDeteccao a partir de "view_date"; cai para "viewed_at" e, por fim, para hoje. */
    private static LocalDate parseData(CSVRecord registro) {
        String viewDate = valor(registro, "view_date", null);
        if (viewDate != null) {
            try {
                return LocalDate.parse(viewDate);
            } catch (RuntimeException ignored) {
                // tenta o próximo
            }
        }
        String viewedAt = valor(registro, "viewed_at", null);
        if (viewedAt != null && viewedAt.length() >= 10) {
            try {
                return LocalDate.parse(viewedAt.substring(0, 10));
            } catch (RuntimeException ignored) {
                // cai no fallback
            }
        }
        return LocalDate.now();
    }

    /** Mês (1..12) a partir de "viewed_at"; usa a data já calculada como fallback. */
    private static int parseMes(CSVRecord registro, LocalDate fallback) {
        String viewedAt = valor(registro, "viewed_at", null);
        if (viewedAt != null) {
            try {
                return LocalDateTime.parse(viewedAt).getMonthValue();
            } catch (RuntimeException ignored) {
                // usa o fallback
            }
        }
        return fallback.getMonthValue();
    }

    /** Lê uma coluna do registro com fallback se ausente/vazia. */
    private static String valor(CSVRecord registro, String coluna, String fallback) {
        if (!registro.isMapped(coluna)) {
            return fallback;
        }
        String v = registro.get(coluna);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }
}
