package com.example.spaceguard.adapters.messaging;

import com.example.spaceguard.adapters.config.RabbitConfig;
import com.example.spaceguard.application.service.FocoIncendioService;
import com.example.spaceguard.domain.foco.dto.FocoIncendioDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor da fila de focos.
 * <p>
 * Recebe cada foco publicado pelo inpe-ingestor e o persiste reutilizando o
 * serviço de CRUD que já existe ({@link FocoIncendioService#criarFocoIncencio}).
 * Roda fora do contexto HTTP/Security — não precisa de JWT.
 * <p>
 * A mensagem JSON é desserializada para {@link FocoIncendioDTO} (os campos batem
 * com o que o producer envia).
 */
@Component
@RequiredArgsConstructor
public class FocoConsumer {

    private static final Logger log = LoggerFactory.getLogger(FocoConsumer.class);

    private final FocoIncendioService focoIncendioService;

    @RabbitListener(queues = RabbitConfig.FILA_FOCOS)
    public void consumir(FocoIncendioDTO foco) {
        focoIncendioService.criarFocoIncencio(foco);
        log.info("Foco gravado via fila: bioma={} municipio={} ({}, {})",
                foco.bioma(), foco.municipio(), foco.latitude(), foco.longitude());
    }
}
