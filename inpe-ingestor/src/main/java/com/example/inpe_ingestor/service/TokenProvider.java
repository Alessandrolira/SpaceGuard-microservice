package com.example.inpe_ingestor.service;

import com.example.inpe_ingestor.client.AuthClient;
import com.example.inpe_ingestor.dto.LoginRequest;
import com.example.inpe_ingestor.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TokenProvider {

    private final AuthClient authClient;
    private final String usuario;
    private final String senha;

    private volatile String token;

    public TokenProvider(AuthClient authClient,
                         @Value("${spaceguard.usuario}") String usuario,
                         @Value("${spaceguard.senha}") String senha) {
        this.authClient = authClient;
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getToken() {
        String atual = token;
        if (atual == null) {
            atual = login();
        }
        return atual;
    }

    public synchronized String login() {
        LoginResponse resp = authClient.login(new LoginRequest(usuario, senha));
        this.token = resp.token();
        return this.token;
    }

    /** Descarta o token em memória (próxima chamada faz login de novo). */
    public synchronized void invalidate() {
        this.token = null;
    }
}
