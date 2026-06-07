package com.example.inpe_ingestor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Configuração do RabbitMQ no lado PRODUTOR.
 * <p>
 * Declara a fila de focos e o conversor JSON (Jackson 3) usado pelo RabbitTemplate
 * para serializar a mensagem. O Spring Boot injeta esse MessageConverter no
 * RabbitTemplate automaticamente.
 */
@Configuration
public class RabbitConfig {

    /** Nome da fila onde os focos são publicados (o consumer usa o mesmo nome). */
    public static final String FILA_FOCOS = "focos.queue";

    @Bean
    public Queue filaFocos() {
        // durable = true: a fila sobrevive a um restart do broker
        return new Queue(FILA_FOCOS, true);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        // reutiliza o JsonMapper do Boot (já configurado p/ java.time etc.)
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
