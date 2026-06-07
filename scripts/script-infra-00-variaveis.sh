#!/usr/bin/env bash
# =====================================================================
# script-infra-00-variaveis.sh
# ---------------------------------------------------------------------
# Variaveis compartilhadas por todos os scripts de infraestrutura.
# NAO contem segredos: senhas/segredos sao passados por variaveis de
# ambiente na hora de rodar (ver README / GUIA-AZURE-DEVOPS.md).
#
# Uso: este arquivo e "carregado" pelos outros scripts com:
#     source ./script-infra-00-variaveis.sh
# =====================================================================

# ---- Identificacao dos recursos (ajuste o SUFIXO para algo unico) ----
# O ACR exige nome GLOBALMENTE unico. Troque "rm560512" pelo seu RM.
export SUFIXO="rm560512"

export LOCATION="eastus2"
export RESOURCE_GROUP="rg-spaceguard-${SUFIXO}"
export ACR_NAME="acrspaceguard${SUFIXO}"          # so letras minusculas e numeros
export ACI_NAME="aci-spaceguard-${SUFIXO}"
export DNS_LABEL="spaceguard-${SUFIXO}"           # vira parte do dominio publico
export IMAGE_TAG="${IMAGE_TAG:-latest}"

# ---- Nomes das imagens dentro do ACR ----
export IMG_SPACEGUARD="spaceguard"
export IMG_INGESTOR="inpe-ingestor"

# ---- Segredos da aplicacao (NUNCA versionar valores reais) ----
# Defina-os no seu terminal ANTES de rodar o deploy, por exemplo:
#   export DB_URL='jdbc:postgresql://SEU-HOST.postgres.database.azure.com:5432/NOME?sslmode=require'
#   export DB_USERNAME='usuario@nomeservidor'   (Single Server usa user@servidor)
#   export DB_PASSWORD='UmaSenhaForte123'
#   export JWT_SECRET='chave-base64-com-32+caracteres'
#   export SPACEGUARD_USER='seu-email@exemplo.com'
#   export SPACEGUARD_PASS='senha-do-usuario-da-api'
#   export OPENAI_API_KEY='sk-...'   (pode ser 'desativado' se nao usar o chat)
export DB_URL="${DB_URL:-}"
export DB_USERNAME="${DB_USERNAME:-}"
export DB_PASSWORD="${DB_PASSWORD:-}"
export JWT_SECRET="${JWT_SECRET:-}"
export SPACEGUARD_USER="${SPACEGUARD_USER:-admin@spaceguard.com}"
export SPACEGUARD_PASS="${SPACEGUARD_PASS:-}"
export OPENAI_API_KEY="${OPENAI_API_KEY:-desativado}"

echo "[infra] Variaveis carregadas:"
echo "        RESOURCE_GROUP = ${RESOURCE_GROUP}"
echo "        LOCATION       = ${LOCATION}"
echo "        ACR_NAME       = ${ACR_NAME}"
echo "        ACI_NAME       = ${ACI_NAME}"
echo "        IMAGE_TAG      = ${IMAGE_TAG}"
