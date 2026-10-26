#!/usr/bin/env bash
# Consulta no SonarCloud o resultado efetivo do quality gate da análise recém-enviada,
# publica as métricas no resumo da execução do GitHub Actions e falha se o gate não passar.
#
# Variáveis: SONAR_TOKEN (obrigatória), COMMIT_ANALISADO, PULL_REQUEST (opcional).
set -euo pipefail

RELATORIO=target/sonar/report-task.txt
SAIDA=target/quality-gate
RESUMO=${GITHUB_STEP_SUMMARY:-/dev/stdout}
TENTATIVAS=60
INTERVALO=5

if [ ! -f "$RELATORIO" ]; then
  echo "::error::$RELATORIO não encontrado: a análise não foi enviada ao SonarCloud."
  exit 1
fi

propriedade() { grep "^$1=" "$RELATORIO" | cut -d= -f2-; }
api() { curl -fsS -u "${SONAR_TOKEN}:" "$1"; }

SERVIDOR=$(propriedade serverUrl)
PROJETO=$(propriedade projectKey)
PAINEL=$(propriedade dashboardUrl)
TAREFA=$(propriedade ceTaskUrl)
mkdir -p "$SAIDA"

# 1. Aguarda o SonarCloud terminar de processar a análise.
STATUS_TAREFA=PENDING
for _ in $(seq "$TENTATIVAS"); do
  api "$TAREFA" > "$SAIDA/tarefa.json"
  STATUS_TAREFA=$(jq -r '.task.status' "$SAIDA/tarefa.json")
  case "$STATUS_TAREFA" in
    SUCCESS) break ;;
    FAILED | CANCELED)
      echo "::error::Processamento da análise no SonarCloud terminou com status $STATUS_TAREFA."
      exit 1 ;;
  esac
  sleep "$INTERVALO"
done
if [ "$STATUS_TAREFA" != SUCCESS ]; then
  echo "::error::Tempo esgotado aguardando o SonarCloud processar a análise."
  exit 1
fi
ANALISE=$(jq -r '.task.analysisId' "$SAIDA/tarefa.json")

# 2. Resultado do quality gate desta análise.
api "$SERVIDOR/api/qualitygates/project_status?analysisId=$ANALISE" > "$SAIDA/quality-gate.json"
GATE=$(jq -r '.projectStatus.status' "$SAIDA/quality-gate.json")

# 3. Métricas da branch ou do pull request analisado.
if [ -n "${PULL_REQUEST:-}" ]; then
  ESCOPO="pullRequest=$PULL_REQUEST"
else
  ESCOPO="branch=${GITHUB_REF_NAME:-main}"
fi
METRICAS=coverage,bugs,reliability_rating,vulnerabilities,security_rating,security_hotspots,code_smells,sqale_rating,duplicated_lines_density,complexity,cognitive_complexity,ncloc
api "$SERVIDOR/api/measures/component?component=$PROJETO&$ESCOPO&metricKeys=$METRICAS" > "$SAIDA/metricas.json"

valor() {
  jq -r --arg m "$1" '(.component.measures[] | select(.metric == $m) | .value) // "-"' "$SAIDA/metricas.json"
}
nota() {
  case "$(valor "$1")" in
    1.0) echo A ;; 2.0) echo B ;; 3.0) echo C ;; 4.0) echo D ;; 5.0) echo E ;; *) echo - ;;
  esac
}

if [ "$GATE" = OK ]; then ICONE="✅ APROVADO"; else ICONE="❌ REPROVADO ($GATE)"; fi

{
  echo "## Quality gate SonarCloud: $ICONE"
  echo
  echo "- **Análise:** [$PROJETO]($PAINEL) (id \`$ANALISE\`)"
  echo "- **Commit analisado:** \`${COMMIT_ANALISADO:-desconhecido}\`"
  echo "- **Escopo:** \`$ESCOPO\`"
  echo
  echo "| Medida | Valor |"
  echo "|--------|-------|"
  echo "| Cobertura | $(valor coverage)% |"
  echo "| Confiabilidade | $(nota reliability_rating) — $(valor bugs) bug(s) |"
  echo "| Segurança | $(nota security_rating) — $(valor vulnerabilities) vulnerabilidade(s), $(valor security_hotspots) hotspot(s) |"
  echo "| Manutenibilidade | $(nota sqale_rating) — $(valor code_smells) code smell(s) |"
  echo "| Duplicações | $(valor duplicated_lines_density)% |"
  echo "| Complexidade ciclomática | $(valor complexity) |"
  echo "| Complexidade cognitiva | $(valor cognitive_complexity) |"
  echo "| Linhas de código | $(valor ncloc) |"
  echo
  echo "### Condições do quality gate"
  echo
  echo "| Métrica | Resultado | Valor | Limite |"
  echo "|---------|-----------|-------|--------|"
  jq -r '.projectStatus.conditions[]? |
    "| \(.metricKey) | \(.status) | \(.actualValue // "-") | \(.comparator) \(.errorThreshold) |"' \
    "$SAIDA/quality-gate.json"
} >> "$RESUMO"

if [ "$GATE" != OK ]; then
  echo "::error::Quality gate reprovado ($GATE). Detalhes: $PAINEL"
  exit 1
fi
echo "Quality gate aprovado: $PAINEL"
