package com.example.spaceguard;

import com.example.spaceguard.application.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios do JwtUtil (geracao/validacao de token JWT).
 * Nao sobe contexto Spring nem precisa de banco/RabbitMQ — roda rapido
 * no CI e produz resultados JUnit para a pipeline publicar.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // chave base64 (>= 32 bytes para HS256), apenas para teste
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "TEvfLJuFhGsg16Do5wgYvu000Z7uoey19EqR5etCWx0=");
        ReflectionTestUtils.setField(jwtUtil, "expirationMinutes", 60);
    }

    @Test
    void deveGerarTokenEExtrairUsuario() {
        String token = jwtUtil.generateToken("alessandro@fiap.com");
        assertNotNull(token);
        assertEquals("alessandro@fiap.com", jwtUtil.extractUsername(token));
    }

    @Test
    void tokenGeradoDeveSerValido() {
        String token = jwtUtil.generateToken("user@fiap.com");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void tokenInvalidoDeveSerRejeitado() {
        assertFalse(jwtUtil.isTokenValid("token.invalido.qualquer"));
    }
}
