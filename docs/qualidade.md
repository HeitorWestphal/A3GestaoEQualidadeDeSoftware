# Registro de qualidade (análise estática)

Histórico das análises do SonarCloud, dos problemas encontrados e das correções. Serve de evidência para os itens 2.7, 2.10 e 2.12 do relatório (evolução antes e depois do quality gate).

- **Painel do projeto:** <https://sonarcloud.io/dashboard?id=HeitorWestphal_A3GestaoEQualidadeDeSoftware>
- **Configuração:** [sonarcloud.md](sonarcloud.md)

## Análises

As notas vão de A (melhor) a E (pior). Bugs, vulnerabilidades e code smells contam o código inteiro. O quality gate avalia o **código novo** de cada análise.

| # | Data | Commit | Execução do CI | Quality gate | Cobertura | Confiabilidade | Segurança | Manutenibilidade | Duplicações | Complexidade (ciclomática / cognitiva) |
|---|------|--------|----------------|--------------|-----------|----------------|-----------|------------------|-------------|----------------------------------------|
| 1 | 26/10/2026 | `b113a77` (`main`, workflow antigo) | — | Não verificado | 99,2% | **D** (2 bugs) | A (0 vulnerabilidades) | A (1 code smell) | 0,0% | 47 / 23 |
| 2 | 26/10/2026 | PR #11 (`c4014a8`) | [40421111136](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/runs/40421111136) | ✅ Aprovado | 99,2% | A | A | A (0 code smells no código novo) | 0,0% | — |
| 3 | 09/11/2026 | `28ac81d` (`main`, merge do PR #11) | [42714297226](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/runs/42714297226) | ✅ Aprovado | 99,2% | **D** (2 bugs) | A (0 vulnerabilidades, 0 hotspots) | A (1 code smell) | 0,0% | 47 / 23 |
| 4 | 09/11/2026 | PR #12 (`6d83d22`) | [42714590543](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/runs/42714590543) | ✅ Aprovado | 99,2% | A (0 bugs) | A | A (0 code smells) | 0,0% | — |
| 5 | 09/11/2026 | `543f65a` (`main`, merge do PR #12) | [42715498535](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/runs/42715498535) | ✅ Aprovado | 99,2% | **A** (0 bugs) | A (0 vulnerabilidades, 0 hotspots) | **A** (0 code smells) | 0,0% | 47 / 23 |

A análise 3 é a primeira da versão integrada na `main` com o job de quality gate. O gate passou porque o código novo estava limpo, mas o código existente ainda tinha confiabilidade **D** por causa de dois bugs nos testes. O PR #12 corrigiu esses problemas. Na análise 5, a versão integrada da `main` ficou com **nota A em confiabilidade, segurança e manutenibilidade e nenhum problema aberto** no SonarCloud.

## Problemas encontrados e correções

| # | Regra | Tipo / severidade | Arquivo | Problema | Correção | Responsável |
|---|-------|-------------------|---------|----------|----------|-------------|
| 1 | shelldre:S7688 (5×) | Code smell / Major | `.github/scripts/quality-gate.sh` | Uso de `[ ]` em vez de `[[ ]]` nas condições | `c4014a8`, no PR #11 | Heitor |
| 2 | java:S5863 | Bug / Major | `LivroTest.java:25` | `assertEquals(livro, livro)` compara a expressão com ela mesma, então o teste nunca falha | `e1784b6`, no PR #12: teste próprio com outra referência ao objeto | Heitor |
| 3 | java:S5845 | Bug / Critical | `LivroTest.java:33` | `assertNotEquals` entre `Livro` e `String`, que passa sempre por serem tipos diferentes | `e1784b6`, no PR #12: teste próprio de `equals` com objeto de outro tipo | Heitor |
| 4 | java:S2699 | Code smell / Blocker | `AppTest.java:15` | Teste sem nenhuma asserção | `6d83d22`, no PR #12: `assertDoesNotThrow` | Heitor |

**Lição aprendida:** os três problemas Java estavam nos **testes**, não no código de produção. A cobertura já era de 99%, mas dois testes não verificavam nada de fato. Ou seja, cobertura alta não garante testes úteis, e a análise estática ajudou a encontrar testes fracos.
