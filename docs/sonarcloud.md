# Análise estática com SonarCloud

A análise estática e o quality gate rodam no job **"Análise estática e quality gate (SonarCloud)"** do workflow [`ci.yml`](../.github/workflows/ci.yml), a cada push na `main` e em todo pull request.

## Como o job funciona

1. **Verifica a configuração.** Sem o secret `SONAR_TOKEN`, o job **falha** com a mensagem "a análise estática NÃO foi executada e o quality gate NÃO foi verificado". Uma execução sem análise nunca aparece como aprovada.
2. **Envia a análise.** Roda `./mvnw verify` (testes e relatório JaCoCo) e o `sonar-maven-plugin`, que manda código e cobertura ao SonarCloud.
3. **Consulta o quality gate.** O script [`quality-gate.sh`](../.github/scripts/quality-gate.sh) espera o SonarCloud processar a análise, consulta pela API o **resultado efetivo do gate daquela análise** e publica no resumo da execução:
   - link da análise e commit analisado;
   - resultado do gate e de cada condição;
   - cobertura, confiabilidade (bugs), segurança (vulnerabilidades e hotspots), manutenibilidade (code smells), duplicações e complexidade.

   Se o gate não estiver `OK`, o job falha.
4. **Guarda as evidências.** O artefato `evidencias-sonarcloud` da execução guarda o `report-task.txt` e os JSONs retornados pela API.

## Configuração (feita uma vez pelo responsável de DevOps)

Nenhuma credencial fica no repositório. O token existe só como secret do GitHub.

1. Entrar em <https://sonarcloud.io> com a conta do GitHub do dono do repositório.
2. **+ → Analyze new project**, importar a organização do GitHub e selecionar o repositório `A3GestaoEQualidadeDeSoftware`.
3. Conferir se a **organization key** e a **project key** mostradas batem com as propriedades `sonar.organization` e `sonar.projectKey` do [`pom.xml`](../pom.xml). Se forem diferentes, ajustar o `pom.xml`.
4. Em **Administration → Analysis Method**, **desativar a "Automatic Analysis"**. A análise é feita pelo CI, que envia a cobertura do JaCoCo.
5. Em **My Account → Security**, gerar um token.
6. No GitHub: **Settings → Secrets and variables → Actions → New repository secret**, nome `SONAR_TOKEN`, valor = token gerado.
7. Rodar o workflow de novo (**Actions → CI → Re-run jobs**) e conferir o resumo do job.

## Quality gate

É usado o quality gate padrão do SonarCloud (*Sonar way*), aplicado ao código novo. Entre as condições estão cobertura mínima de 80% e no máximo 3% de linhas duplicadas no código novo, além de security hotspots revisados. As condições efetivamente avaliadas em cada análise, com valor e limite, aparecem na tabela "Condições do quality gate" do resumo do job.

Além do gate, o build local e o CI exigem **cobertura mínima de 75% de linhas** no projeto inteiro (JaCoCo, no `pom.xml`).

## Registro de problemas e correções

Os problemas apontados pelo SonarCloud e as correções feitas são registrados em [`docs/qualidade.md`](qualidade.md), com link para a análise antes e depois da correção.
