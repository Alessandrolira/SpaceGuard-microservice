#!/usr/bin/env bash
# =====================================================================
# script-infra-01-base.sh
# ---------------------------------------------------------------------
# Provisiona os recursos DURAVEIS de nuvem que o Build e a Release usam:
#   - Resource Group
#   - Azure Container Registry (ACR) com admin habilitado
#
# Pre-requisitos: estar logado -> az login
# Uso:            bash scripts/script-infra-01-base.sh
# =====================================================================
set -euo pipefail

cd "$(dirname "$0")"
source ./script-infra-00-variaveis.sh

echo "==> Registrando resource providers (necessario em assinaturas novas; pode levar 1-2 min)"
az provider register --namespace Microsoft.ContainerRegistry --wait
az provider register --namespace Microsoft.ContainerInstance --wait

echo "==> Criando Resource Group: ${RESOURCE_GROUP}"
az group create \
  --name "${RESOURCE_GROUP}" \
  --location "${LOCATION}" \
  --output table

echo "==> Criando Azure Container Registry: ${ACR_NAME}"
az acr create \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${ACR_NAME}" \
  --location "${LOCATION}" \
  --sku Basic \
  --admin-enabled true \
  --output table

echo ""
echo "==> Recursos base criados com sucesso!"
echo "    ACR login server: $(az acr show -n "${ACR_NAME}" --query loginServer -o tsv)"
echo ""
echo "Proximo passo: bash scripts/script-infra-02-deploy.sh"
