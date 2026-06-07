package com.example.spaceguard.adapters.controller;

import com.example.spaceguard.application.service.ChatService;
import com.example.spaceguard.domain.chat.dto.ChatRequest;
import com.example.spaceguard.domain.chat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint do assistente de IA (Spring AI).
 * <p>
 * POST /chat -> responde perguntas usando os focos do banco como contexto.
 * Protegido por JWT (igual aos demais endpoints).
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.responder(request.message()));
    }
}
