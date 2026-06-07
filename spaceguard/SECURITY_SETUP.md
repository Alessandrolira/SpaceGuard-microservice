# Spring Security - SpaceGuard

Implementação de Spring Security com autenticação baseada em JWT, replicada a partir do padrão estabelecido no projeto PetCare.

## Estrutura Implementada

### Arquivos de Segurança

```
src/main/java/com/example/spaceguard/
├── infra/security/
│   ├── SecurityConfig.java                 # Configuração principal do Spring Security
│   ├── JwtService.java                     # Serviço de geração e parse de tokens JWT
│   ├── JwtAuthenticationFilter.java        # Filter para autenticação com JWT
│   ├── AuthenticatedUser.java              # Classe que representa o usuário autenticado
│   ├── RestAuthenticationEntryPoint.java   # Handler para exceções de autenticação
│   └── RestAccessDeniedHandler.java        # Handler para acesso negado
├── domain/usuario/
│   ├── Usuario.java                        # Entidade Usuario/User
│   └── UserRole.java                       # Enum com papéis (ADMIN, USER)
```

## Configuração

### 1. Propriedades (application.yaml)

```yaml
security:
  jwt:
    secret: "sua-chave-secreta-com-pelo-menos-32-caracteres"  # Mínimo 32 caracteres
    expiration-minutes: 60                                      # Tempo de expiração do token

app:
  cors:
    allowed-origin-patterns:
      - "http://localhost:*"
      - "http://127.0.0.1:*"
      - "http://192.168.*:*"
```

### 2. Endpoints Públicos (sem autenticação)

- `POST /auth/login` - Login (retorna JWT token)
- `POST /usuario/criar` - Criar novo usuário
- `OPTIONS /**` - CORS preflight requests

Todos os outros endpoints requerem autenticação.

## Como Usar

### 1. Login e Obter Token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@teste.com",
    "password": "senha123"
  }'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Usar o Token em Requisições Autenticadas

```bash
curl -X GET http://localhost:8080/api/protected \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. Acessar Dados do Usuário Autenticado no Controller

```java
import com.example.spaceguard.infra.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
public class MeuController {
    
    @GetMapping("/perfil")
    public ResponseEntity<?> obterPerfil(Authentication authentication) {
        AuthenticatedUser usuario = (AuthenticatedUser) authentication.getPrincipal();
        
        String userId = usuario.getUserId();
        String email = usuario.getEmail();
        String nome = usuario.getName();
        UserRole role = usuario.getRole();
        
        return ResponseEntity.ok(usuario);
    }
}
```

## Estrutura do JWT Token

O token JWT contém os seguintes claims:

```json
{
  "sub": "usuario@teste.com",
  "role": "USER",
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "email": "usuario@teste.com",
  "name": "Nome do Usuário",
  "iat": 1234567890,
  "exp": 1234571490
}
```

## Fluxo de Autenticação

1. **Login** → Usuário envia email e senha
2. **Validação** → Backend valida credenciais
3. **Token Gerado** → JwtService cria um token JWT assinado com HS256
4. **Cliente Armazena** → Front-end armazena o token
5. **Requisições Autenticadas** → Cliente envia token no header `Authorization: Bearer <token>`
6. **Filter Valida** → JwtAuthenticationFilter intercepta a requisição, parse o token e autenticam o usuário
7. **Acesso Concedido** → Se válido, requisição é processada; se inválido, erro 401

## Permissões por Role

### Configurar Permissões no SecurityConfig

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
    .requestMatchers(HttpMethod.POST, "/usuario/criar").permitAll()
    // Adicione mais rotas públicas conforme necessário
    .anyRequest().authenticated()
)
```

### Usar @PreAuthorize para Controlar Acesso

```java
@GetMapping("/admin/relatorio")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> gerarRelatorio() {
    return ResponseEntity.ok("Relatório administrativo");
}
```

## Tratamento de Erros

- **401 Unauthorized** → Token ausente ou inválido
- **403 Forbidden** → Usuário autenticado mas sem permissão

## Segurança

- **Secret Key** → Mínimo 32 caracteres para HS256
- **Password Encoder** → BCryptPasswordEncoder (usar para armazenar senhas)
- **HTTPS** → Em produção, sempre use HTTPS
- **Token Expiration** → Defina um tempo apropriado para expiração

## Próximos Passos

1. Criar um endpoint `/auth/login` que valide credenciais
2. Implementar repositório de usuários
3. Integrar com seu banco de dados (PostgreSQL, Oracle, etc)
4. Configurar refresh tokens (opcional)
5. Implementar logout/blacklist de tokens (opcional)

## Referências

- Padrão baseado em: `https://github.com/petcare-alekao/backend-petcare`
- JWT: `https://jwt.io/`
- Spring Security: `https://spring.io/projects/spring-security`
