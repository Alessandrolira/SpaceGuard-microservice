-- =====================================================================
-- script-bd.sql  —  DDL do banco de dados SpaceGuard (PostgreSQL)
-- =====================================================================
-- Cria todas as tabelas usadas pelo microsserviço "spaceguard".
-- A aplicacao roda com spring.jpa.hibernate.ddl-auto = none, ou seja,
-- o Hibernate NAO cria as tabelas: este script e a unica fonte do schema.
--
-- Os nomes de coluna seguem a estrategia de nomenclatura padrao do
-- Spring Boot (camelCase -> snake_case). Ex.: nomeUsuario -> nome_usuario.
--
-- Este arquivo e copiado para /docker-entrypoint-initdb.d/ na imagem
-- Postgres (dockerfiles/postgres.Dockerfile), entao o banco executa o DDL
-- automaticamente na primeira inicializacao do container.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tabela: usuario
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario     VARCHAR(36)  NOT NULL,
    nome_usuario   VARCHAR(255),
    telefone       VARCHAR(255),
    email          VARCHAR(255),
    senha          VARCHAR(255),
    data_cadastro  DATE,
    role           SMALLINT,                -- enum UserRole (ORDINAL): 0=ADMIN, 1=USER
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario)
);

-- ---------------------------------------------------------------------
-- Tabela: risco
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS risco (
    id_risco     VARCHAR(36)   NOT NULL,
    nivel_risco  VARCHAR(255),               -- enum EnumNivelRisco (STRING): ALTO/MEDIO/BAIXO
    pontuacao    NUMERIC(12,2),
    CONSTRAINT pk_risco PRIMARY KEY (id_risco)
);

-- ---------------------------------------------------------------------
-- Tabela: foco_incendio
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS foco_incendio (
    id_foco         VARCHAR(36)   NOT NULL,
    data_deteccao   DATE,
    latitude        NUMERIC(10,6),
    longitude       NUMERIC(10,6),
    risco_fogo      NUMERIC(5,2),
    bioma           VARCHAR(255),
    municipio       VARCHAR(255),
    estado          VARCHAR(255),
    foco_ativo      BOOLEAN,
    risco_id_risco  VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_foco_incendio PRIMARY KEY (id_foco),
    CONSTRAINT fk_foco_risco
        FOREIGN KEY (risco_id_risco) REFERENCES risco (id_risco)
);

-- ---------------------------------------------------------------------
-- Tabela: local_usuario
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS local_usuario (
    id_local            VARCHAR(36)   NOT NULL,
    latitude            NUMERIC(10,6),
    longitude           NUMERIC(10,6),
    data_registro       DATE,
    usuario_id_usuario  VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_local_usuario PRIMARY KEY (id_local),
    CONSTRAINT fk_local_usuario
        FOREIGN KEY (usuario_id_usuario) REFERENCES usuario (id_usuario)
);

-- ---------------------------------------------------------------------
-- Tabela: alerta
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS alerta (
    id_alerta               VARCHAR(36)   NOT NULL,
    titulo_alerta           VARCHAR(255),
    msg_alerta              VARCHAR(255),
    distancia               NUMERIC(10,2),
    data_emissao            DATE,
    data_expir              DATE,
    usuario_id_usuario      VARCHAR(36)   NOT NULL,
    foco_incendio_id_foco   VARCHAR(36)   NOT NULL,
    CONSTRAINT pk_alerta PRIMARY KEY (id_alerta),
    CONSTRAINT fk_alerta_usuario
        FOREIGN KEY (usuario_id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_alerta_foco
        FOREIGN KEY (foco_incendio_id_foco) REFERENCES foco_incendio (id_foco)
);

-- =====================================================================
-- Fim do script-bd.sql
-- =====================================================================
