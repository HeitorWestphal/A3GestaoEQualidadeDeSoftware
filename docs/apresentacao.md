# Roteiro da apresentação (30/11/2026)

Tempo total de **10 minutos** (variação de 2 min), cerca de **1min40s por integrante** (6 integrantes). Todos falam e **todos demonstram uma parte do software em execução**, que vale 1,5 ponto.

**Abertura obrigatória (0,5 ponto):** cada integrante se apresenta com nome, semestre e curso.

| # | Slide | Quem | Tempo | Conteúdo |
|---|-------|------|-------|----------|
| 1 | Capa e identificação | Todos | 0:40 | Título, nome, semestre e curso de cada um |
| 2 | Sumário + requisitos funcionais e não funcionais | Heitor | 0:40 | RF01 a RF06, RN01 a RN06, requisitos de qualidade |
| 3 | Demo: cadastro de livro | Heitor | 0:40 | Cadastro válido e recusa de ISBN inválido ou duplicado |
| 4 | Demo: cadastro de leitor | João Víctor | 1:00 | Cadastro e validação de CPF |
| 5 | Demo: consulta ao acervo | Ian | 1:00 | Busca por título, autor e ISBN com disponibilidade |
| 6 | Demo: empréstimo | João Gabriel | 1:10 | Empréstimo permitido e recusas (indisponível, limite, pendência) |
| 7 | Demo: devolução e multa | Bruno | 0:50 | Devolução no prazo e com multa |
| 8 | Demo: relatório de atrasos | João Pedro | 0:50 | Empréstimos em atraso, multa prevista e histórico do leitor |
| 9 | Integração contínua e testes automatizados | Bruno | 0:40 | Execução do GitHub Actions, testes e relatório JaCoCo |
| 10 | Principais issues detectadas e corrigidas | João Gabriel | 0:30 | Issues do professor e defeitos encontrados |
| 11 | Evolução: antes e depois do quality gate | Ian | 0:40 | Painel do SonarCloud antes e depois das correções |
| 12 | Conclusão | João Pedro e João Víctor | 0:40 | Resultados e lições aprendidas |

**Preparação:**

- [ ] Slides prontos e exportados em PDF (entregar junto no .zip do Ulife).
- [ ] Ensaiar cronometrando: 10 min ± 2.
- [ ] Deixar a aplicação compilada (`./mvnw verify`) e dados de exemplo prontos para a demonstração.
- [ ] Abas abertas: Actions (última execução verde), SonarCloud e issues fechadas.
