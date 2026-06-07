# =====================================================================
# Dockerfile do banco PostgreSQL com o schema do SpaceGuard ja embutido.
# O script DDL e copiado para /docker-entrypoint-initdb.d/, pasta que a
# imagem oficial do Postgres executa AUTOMATICAMENTE na primeira subida
# do container (quando o volume de dados esta vazio).
#
# Build context esperado: RAIZ do repositorio
#   docker build -f dockerfiles/postgres.Dockerfile -t spaceguard-db .
# =====================================================================
FROM postgres:16

# DDL das tabelas (rodado uma unica vez, na inicializacao do banco vazio)
COPY scripts/script-bd.sql /docker-entrypoint-initdb.d/01-script-bd.sql
