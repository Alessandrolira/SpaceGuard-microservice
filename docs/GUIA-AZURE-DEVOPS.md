# Guia de Entrega — DevOps (Azure DevOps + Azure)

Este guia cobre, na ordem, **tudo** que precisa ser feito para a GS de DevOps.
Os arquivos do repositório (scripts, Dockerfiles, pipeline, DDL, JSONs, desenho)
já estão prontos. Aqui estão as etapas **manuais** (portal) e os comandos.

> ✅ Já configurado: o sufixo **`rm560512`** (seu RM) está aplicado em
> `scripts/script-infra-00-variaveis.sh` e em `azure-pipeline.yml`, e o `.git`
> aninhado do `spaceguard/` já foi removido (repositório único, pronto pro push).
> Professor a convidar: **profantonio.figueiredo@fiap.com.br**.

---

## 0) Pré-requisitos
- Conta **Azure** (ex.: *Azure for Students*) com uma assinatura ativa.
- Conta **Azure DevOps** (https://dev.azure.com) com o mesmo login.
- **Azure CLI** instalada (ou usar o **Azure Cloud Shell** no portal — já vem com `az`, `git`, `envsubst`).
- **E-mail do professor**: `profantonio.figueiredo@fiap.com.br` (para o convite).

---

## 1) Provisionar a infra base na Azure (Azure CLI)
No seu terminal (ou Cloud Shell), logado:

```bash
az login
bash scripts/script-infra-01-base.sh
```

Isso cria o **Resource Group** e o **ACR**. (O deploy completo do app é feito
depois, automaticamente, pela pipeline de Release — ou manualmente com
`scripts/script-infra-02-deploy.sh` se quiser testar antes.)

---

## 2) Criar a Organização e o Projeto no Azure DevOps
1. Acesse https://dev.azure.com e crie/usar uma **Organização**.
2. **New project** → nome `SpaceGuard-DevOps` → **Visibility: Private** (obrigatório, item 3.1).
3. Anote os links da **Organização** e do **Projeto** (vão no PDF).

### 2.1) Convidar o professor (item 2.2)
- **Organization settings → Users → Add users**: `profantonio.figueiredo@fiap.com.br`,
  **Access level: Basic**, e adicione ao projeto.
- **Project settings → Permissions**: coloque o professor no grupo **Contributors**.

---

## 3) Subir o código no Azure Repos (item 2.3)
> ✅ O `.git` aninhado do `spaceguard/` já foi removido e o `spaceguard` já é
> rastreado como arquivos normais (não é mais submódulo) — o push já leva todo
> o código-fonte.

No **Project settings → Repos** (ou na tela inicial do Repos) copie a URL do repo
e faça o push do projeto inteiro:

```bash
# na raiz do projeto (java2)
git add .
git commit -m "DevOps: scripts, dockerfiles, pipeline e docs"
git remote add azure https://dev.azure.com/SUA-ORG/SpaceGuard-DevOps/_git/SpaceGuard-DevOps
git push -u azure main
```

---

## 4) Azure Boards — criar a Tarefa inicial (itens 2.4 / 3.2)
1. **Boards → Work items → New Work Item → Task**: ex. *"Provisionar deploy em nuvem"*.
2. Anote o **ID** (ex. `#1`). Você vai referenciá-lo nos commits com `#1`.

---

## 5) Proteger a branch `main` (itens 3.3 / 3.4 / 3.5 / penалidade 17)
**Project settings → Repos → Repositories → (seu repo) → Policies → Branch policies → main**:
- ✅ **Require a minimum number of reviewers** = 1
  - ✅ marque **Allow requestors to approve their own changes** (item 3.5 — você aprova sua própria PR)
- ✅ **Check for linked work items** = Required (item 3.3)
- ✅ **Automatically include reviewers** → adicione **você mesmo (seu RM)** como revisor padrão (item 3.3)

Com isso, `main` só recebe código via **Pull Request aprovado** → o Build dispara
**somente após o Merge** (item 3.4).

---

## 6) Criar o Service Connection (a pipeline usa para falar com a Azure)
**Project settings → Service connections → New → Azure Resource Manager →
Workload identity federation (automatic)** → selecione sua assinatura →
**Service connection name: `svc-azure-spaceguard`** (tem que bater com o `azure-pipeline.yml`).

---

## 7) Criar o grupo de variáveis com os segredos (item 3.15 / penalidade 16)
**Pipelines → Library → + Variable group → nome: `spaceguard-secrets`**.
Adicione as variáveis e clique no **cadeado** para marcar como *secret*:

| Variável          | Exemplo de valor                                   |
|-------------------|----------------------------------------------------|
| `DB_PASSWORD`     | `UmaSenhaForte123`                                  |
| `JWT_SECRET`      | uma chave base64 com 32+ bytes (ex. gere uma)      |
| `SPACEGUARD_PASS` | `SenhaForte123` (a mesma senha do usuário da API)  |
| `OPENAI_API_KEY`  | `desativado` (se não for demonstrar o chat)        |

> Gerar um JWT_SECRET base64 rápido: `openssl rand -base64 32`

---

## 8) Criar a Pipeline (CI/CD) (itens 2.5 / 2.6)
**Pipelines → New pipeline → Azure Repos Git → (seu repo) → Existing Azure Pipelines YAML file →
`/azure-pipeline.yml` → Run**.

- A 1ª execução pode pedir **permissão** para usar o Service Connection e o Variable group → **Permit**.
- Vai pedir para criar o **Environment** `spaceguard-producao` (do estágio de Release) — autorize.

A partir daí, **todo merge na `main` dispara CI → CD automaticamente**.

---

## 9) Verificar o deploy
Ao fim da Release, o log mostra a URL pública. Teste:
- Swagger: `http://<dns>.<regiao>.azurecontainer.io:8080/swagger-ui.html`
- (a 1ª subida demora ~1–2 min: o spaceguard reinicia até o Postgres ficar pronto)

---

## 10) Roteiro do VÍDEO (siga na ordem — cada item omitido = −20 pts)
Grave **contínuo, com narração por voz, 720p+, áudio claro** (sem legendas no lugar da fala):

1. **README + arquitetura**: explique a solução, o conceito e o desenho (`docs/arquitetura.svg`).
2. **Portal Azure**: mostre os recursos criados pelos scripts (RG, ACR, Container Group).
3. **Boards**: crie uma **nova Task** ao vivo.
4. **Branch + alteração de código**: crie uma branch, faça uma alteração **em código-fonte de verdade**
   (NÃO só no README — penalidade 20), commit referenciando a task (`#ID`).
5. **PR + Merge na main**: abra a PR, vincule o work item, aprove e faça o merge.
6. **Pipelines**: mostre **CI e CD rodando automaticamente** após o merge, etapa por etapa.
7. **Artefatos**: destaque o artefato **pacote** (JARs) e os **testes JUnit** publicados.
8. **App em nuvem**: mostre a alteração do passo 4 publicada e funcionando na URL pública.
9. **CRUD (2 tabelas)**: faça **Create, Read, Update, Delete** em `risco` e `foco_incendio`
   (use os JSONs de `crud-json/`). **Comprove no banco com SELECT** (não use GET — penalidade 15):
   ```bash
   # abrir psql dentro do container do banco:
   az container exec -g rg-spaceguard-rm560512 -n aci-spaceguard-rm560512 \
     --container-name postgres --exec-command "psql -U spaceguard -d spaceguard"
   # depois:  SELECT * FROM risco;   SELECT * FROM foco_incendio;
   ```
10. **Encerramento**: mostre a Task concluída com os links (commits, PR, etc.).

---

## 11) PDF de entrega
Inclua: folha de rosto (grupo, **RM + nome completo** de cada integrante),
**link da Organização**, **link do Projeto** e **link do vídeo no YouTube**.

---

## Mapa rápido requisito → onde está
| Requisito | Onde |
|---|---|
| Scripts Azure CLI (`script-infra*`) | `scripts/` |
| `script-bd.sql` (DDL) | `scripts/script-bd.sql` |
| Dockerfiles | `dockerfiles/` |
| `azure-pipeline.yml` | raiz |
| CRUD em JSON | `crud-json/` + README |
| Variáveis de ambiente / segredos | Variable group + `application.yaml` (sem segredos hardcoded) |
| Desenho da arquitetura | `docs/arquitetura.svg` |
| Banco em container | container `postgres` no ACI |
| Deploy em container | Container Group (ACI) a partir do ACR |
