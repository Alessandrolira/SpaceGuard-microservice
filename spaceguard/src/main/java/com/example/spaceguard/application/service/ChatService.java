package com.example.spaceguard.application.service;

import com.example.spaceguard.domain.chat.dto.ChatResponse;
import com.example.spaceguard.domain.foco.dto.FocoIncendioResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Assistente de IA (Spring AI + Anthropic Claude).
 * <p>
 * Monta um contexto com os focos de incêndio que já estão no banco e usa esse
 * contexto para responder perguntas do usuário — equivalente ao /chat do app
 * Python do projeto, mas em Java com dados reais do spaceguard.
 */
@Service
public class ChatService {

    /** Limite de focos detalhados no prompt (controla tamanho/custo). */
    private static final int MAX_FOCOS_CONTEXTO = 30;

    private final ChatClient chatClient;
    private final FocoIncendioService focoIncendioService;

    public ChatService(ChatClient.Builder chatClientBuilder, FocoIncendioService focoIncendioService) {
        this.chatClient = chatClientBuilder.build();
        this.focoIncendioService = focoIncendioService;
    }

    public ChatResponse responder(String mensagem) {
        List<FocoIncendioResponseDTO> focos = focoIncendioService.buscarTodosFocos();
        String contexto = montarContexto(focos);

        String systemPrompt = """
                Você é o assistente de IA do SpaceGuard, sistema de monitoramento de queimadas
                no Brasil baseado em focos de incêndio detectados por satélite (INPE).
                Seu papel é ajudar a interpretar os focos, avaliar riscos e sugerir ações.
                Responda em português, de forma clara, objetiva e técnica.
                Use SOMENTE os dados de contexto abaixo como base factual; se a informação não
                estiver no contexto, diga que não há dados suficientes.

                --- CONTEXTO: FOCOS NO BANCO ---
                %s
                --- FIM DO CONTEXTO ---
                """.formatted(contexto);

        String resposta = chatClient.prompt()
                .system(systemPrompt)
                .user(mensagem)
                .call()
                .content();

        return new ChatResponse(resposta, Math.min(focos.size(), MAX_FOCOS_CONTEXTO));
    }

    private String montarContexto(List<FocoIncendioResponseDTO> focos) {
        if (focos.isEmpty()) {
            return "Não há focos de incêndio cadastrados no momento.";
        }

        Map<String, Integer> porNivel = new LinkedHashMap<>();
        Map<String, Integer> porBioma = new LinkedHashMap<>();
        for (FocoIncendioResponseDTO foco : focos) {
            String nivel = (foco.risco() != null && foco.risco().nivelRisco() != null)
                    ? foco.risco().nivelRisco().name() : "DESCONHECIDO";
            porNivel.merge(nivel, 1, Integer::sum);
            porBioma.merge(foco.bioma() == null ? "?" : foco.bioma(), 1, Integer::sum);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total de focos: ").append(focos.size()).append("\n");
        sb.append("Por nível de risco: ").append(porNivel).append("\n");
        sb.append("Por bioma: ").append(porBioma).append("\n");
        sb.append("Amostra de focos:\n");

        focos.stream().limit(MAX_FOCOS_CONTEXTO).forEach(f -> {
            String nivel = (f.risco() != null && f.risco().nivelRisco() != null)
                    ? f.risco().nivelRisco().name() : "?";
            sb.append("  - bioma=").append(f.bioma())
                    .append(" | municipio=").append(f.municipio())
                    .append(" | nivel=").append(nivel)
                    .append(" | riscoFogo=").append(f.riscoFogo())
                    .append(" | coord=(").append(f.latitude()).append(", ").append(f.longitude()).append(")")
                    .append("\n");
        });

        return sb.toString();
    }
}
