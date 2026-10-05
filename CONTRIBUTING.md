# Como contribuir

## Regras da A3

- Cada integrante usa **a própria conta do GitHub** e faz commit **somente dos seus arquivos**.
- Só conta como participação o commit de **código-fonte** (`src/`), não README, `pom.xml` etc.
- Commits **pequenos e atômicos**: uma finalidade por commit (ex.: uma funcionalidade, um teste, uma correção). Nada de "megacommits".

## Fluxo de trabalho

1. Atualize a `main`: `git checkout main && git pull`
2. Crie uma branch: `git checkout -b feat/cadastro-livro`
3. Implemente a funcionalidade **com os testes unitários**.
4. Rode localmente: `./mvnw verify` (testes + cobertura mínima de 75%).
5. Faça commits seguindo a convenção abaixo e envie: `git push -u origin feat/cadastro-livro`
6. Abra um Pull Request para a `main`. O CI (GitHub Actions) precisa passar antes do merge.

Defeitos e tarefas são registrados como **Issues** no GitHub. Referencie a issue no commit ou no PR (`Closes #12`).

## Convenção de commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/pt-br/):

```
<tipo>(<escopo opcional>): <descrição no imperativo, minúscula>
```

| Tipo | Uso |
|------|-----|
| `feat` | nova funcionalidade |
| `fix` | correção de bug |
| `test` | adição ou ajuste de testes |
| `refactor` | mudança de código sem alterar comportamento |
| `docs` | documentação |
| `build` | Maven, dependências |
| `ci` | pipeline do GitHub Actions |
| `chore` | tarefas gerais |

Exemplos:

```
feat(livro): adiciona cadastro de livro com validação de ISBN
test(emprestimo): adiciona teste para limite de 3 empréstimos ativos
fix(devolucao): corrige cálculo de multa por atraso
```
