#!/usr/bin/env bash
# =====================================================================
# script-infra-02-deploy.sh
# ---------------------------------------------------------------------
# 1) Builda as 3 imagens DENTRO do ACR (az acr build, sem precisar de
#    Docker local) a partir dos Dockerfiles em /dockerfiles.
# 2) Gera o aci-deployment.yaml a partir do template (envsubst).
# 3) Cria/atualiza o Container Group (ACI) com os 4 containers.
#
# Pre-requisitos:
#   - az login
#   - ter rodado scripts/script-infra-01-base.sh (RG + ACR ja existem)
#   - exportar os segredos no terminal:
#       export DB_PASSWORD=...  JWT_SECRET=...  SPACEGUARD_PASS=...
#
# Uso: bash scripts/script-infra-02-deploy.sh
# =====================================================================
set -euo pipefail

cd "$(dirname "$0")"
source ./script-infra-00-variaveis.sh

# raiz do repo (build context dos Dockerfiles)
REPO_ROOT="$(cd .. && pwd)"

# ---- Validacao minima de segredos ----
if [[ -z "${DB_URL}" || -z "${DB_USERNAME}" || -z "${DB_PASSWORD}" || -z "${JWT_SECRET}" || -z "${SPACEGUARD_PASS}" ]]; then
  echo "ERRO: defina DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET e SPACEGUARD_PASS antes de rodar."
  exit 1
fi

echo "==> (1/3) Build (Docker) e push das imagens para o ACR (${ACR_NAME})"
# Usa docker build + docker push (em vez de "az acr build"), pois ACR Tasks
# pode estar bloqueado na assinatura (Student/sponsored). Requer Docker local.
# tr -d '\r' remove o CR que o 'az' do Windows adiciona (CRLF) e que quebra as tags
ACR_LS="$(az acr show -n "${ACR_NAME}" --query loginServer -o tsv | tr -d '\r')"
ACR_USER="$(az acr credential show -n "${ACR_NAME}" --query username -o tsv | tr -d '\r')"
ACR_PASS="$(az acr credential show -n "${ACR_NAME}" --query 'passwords[0].value' -o tsv | tr -d '\r')"
# login explicito com as credenciais admin do ACR (confiavel no docker do WSL)
echo "${ACR_PASS}" | docker login "${ACR_LS}" -u "${ACR_USER}" --password-stdin
(
  cd "${REPO_ROOT}"
  docker build -t "${ACR_LS}/${IMG_SPACEGUARD}:${IMAGE_TAG}" -f dockerfiles/spaceguard.Dockerfile .
  docker push  "${ACR_LS}/${IMG_SPACEGUARD}:${IMAGE_TAG}"
  docker build -t "${ACR_LS}/${IMG_INGESTOR}:${IMAGE_TAG}"   -f dockerfiles/inpe-ingestor.Dockerfile .
  docker push  "${ACR_LS}/${IMG_INGESTOR}:${IMAGE_TAG}"
)

echo "==> (2/3) Gerando aci-deployment.yaml a partir do template"
export ACR_LOGIN_SERVER="$(az acr show -n "${ACR_NAME}" --query loginServer -o tsv | tr -d '\r')"
export ACR_USERNAME="$(az acr credential show -n "${ACR_NAME}" --query username -o tsv | tr -d '\r')"
export ACR_PASSWORD="$(az acr credential show -n "${ACR_NAME}" --query 'passwords[0].value' -o tsv | tr -d '\r')"

envsubst < aci-deployment.template.yaml > aci-deployment.yaml

echo "==> (3/3) Criando/atualizando o Container Group (${ACI_NAME})"
# recria do zero para garantir que pega as imagens novas
az container delete -g "${RESOURCE_GROUP}" -n "${ACI_NAME}" --yes 2>/dev/null || true
az container create -g "${RESOURCE_GROUP}" -f aci-deployment.yaml --output none

FQDN="$(az container show -g "${RESOURCE_GROUP}" -n "${ACI_NAME}" \
  --query ipAddress.fqdn -o tsv | tr -d '\r')"

echo ""
echo "==> Deploy concluido!"
echo "    API spaceguard : http://${FQDN}:8080  (Swagger: /swagger-ui.html)"
echo "    inpe-ingestor  : http://${FQDN}:8083  (POST /importar)"
