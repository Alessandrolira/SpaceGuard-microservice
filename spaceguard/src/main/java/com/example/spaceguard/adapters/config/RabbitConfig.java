package com.example.spaceguard.adapters.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Configuração do RabbitMQ no lado CONSUMIDOR.
 * <p>
 * Declara a mesma fila do producer e o conversor JSON (Jackson 3). O
 * {@code TypePrecedence.INFERRED} faz o conversor IGNORAR o cabeçalho __TypeId__
 * que o producer envia (com a classe dele) e desserializar a mensagem para o
 * tipo do parâmetro do {@code @RabbitListener} — assim os dois serviços não
 * precisam compartilhar a mesma classe.
 */
@Configuration
public class RabbitConfig {

    public static final String FILA_FOCOS = "focos.queue";

    @Bean
    public Queue filaFocos() {
        return new Queue(FILA_FOCOS, true);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);
        converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
