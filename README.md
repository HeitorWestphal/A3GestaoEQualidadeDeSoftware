# Sistema de Biblioteca

[![CI](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/workflows/ci.yml/badge.svg)](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/workflows/ci.yml)

Projeto da **A3 – Gestão e Qualidade de Software** (UNISUL, prof. Jorge Werner) — **Tema 4: Sistema de Biblioteca**.

## Integrantes

| Nome | Papel | GitHub |
|------|-------|--------|
| Heitor Westphal | Líder de equipe | [@HeitorWestphal](https://github.com/HeitorWestphal) |
| _a definir_ | Desenvolvedor | |
| _a definir_ | Desenvolvedor | |
| _a definir_ | Testador | |
| _a definir_ | Especialista em DevOps | |

> Todos os integrantes são programadores e desenvolvem testes unitários das suas funcionalidades.

## Problema atendido

Bibliotecas pequenas (escolares, comunitárias, de empresas) costumam controlar acervo e empréstimos em planilhas ou cadernos, o que gera perda de livros, empréstimos sem devolução e falta de informação sobre disponibilidade. O sistema centraliza o cadastro de livros e leitores e controla empréstimos e devoluções aplicando as regras da biblioteca.

## Usuários / atores

- **Bibliotecário** — cadastra livros e leitores, registra empréstimos e devoluções.
- **Leitor** — consulta o acervo e a disponibilidade dos livros.

## Tecnologias previstas

| Finalidade | Ferramenta |
|------------|-----------|
| Linguagem | Java 21 |
| Build e dependências | Maven (via Maven Wrapper `./mvnw`) |
| Testes unitários | JUnit 5 |
| Cobertura de código | JaCoCo (mínimo de 75%) |
| Integração contínua | GitHub Actions |
| Análise estática / quality gate | SonarCloud |
| Controle de versão | Git + GitHub (branch `main`) |

## Requisitos funcionais

- **RF01 – Cadastrar livro:** registrar título, autor, ISBN, ano e quantidade de exemplares.
- **RF02 – Cadastrar leitor:** registrar nome, CPF/matrícula e e-mail.
- **RF03 – Consultar acervo:** buscar livros por título, autor ou ISBN, exibindo a disponibilidade.
- **RF04 – Registrar empréstimo:** emprestar um exemplar disponível a um leitor, com data prevista de devolução.
- **RF05 – Registrar devolução:** dar baixa no empréstimo e calcular multa por atraso, quando houver.

### Regras de negócio

- **RN01:** o ISBN deve ser válido e único no acervo.
- **RN02:** só é possível emprestar livro com exemplar disponível.
- **RN03:** cada leitor pode ter no máximo 3 empréstimos ativos.
- **RN04:** o prazo de empréstimo é de 7 dias.
- **RN05:** leitor com empréstimo em atraso não pode fazer novos empréstimos.
- **RN06:** multa de R$ 1,00 por dia de atraso na devolução.

## Requisitos de qualidade prioritários

- **Confiabilidade:** regras de negócio cobertas por testes unitários automatizados; cobertura mínima de 75%.
- **Manutenibilidade:** código aprovado no quality gate do SonarCloud (sem bugs, vulnerabilidades ou duplicações relevantes).
- **Segurança:** validação de todas as entradas; dados pessoais dos leitores (CPF, e-mail) tratados conforme a LGPD.
- **Usabilidade:** mensagens de erro claras para o bibliotecário.

## Fluxo principal — Empréstimo de livro

1. O bibliotecário informa o leitor e o livro.
2. O sistema verifica se o leitor existe e pode pegar livros (RN03, RN05).
3. O sistema verifica se há exemplar disponível (RN02).
4. O sistema registra o empréstimo com data prevista de devolução (RN04) e diminui a quantidade disponível.

### Critérios de aceitação

- **Dado** um leitor sem pendências e um livro com exemplar disponível, **quando** o empréstimo for registrado, **então** ele é salvo com devolução prevista para 7 dias e a disponibilidade diminui em 1.
- **Dado** um livro sem exemplares disponíveis, **quando** tentar emprestar, **então** o sistema recusa com a mensagem "Livro indisponível".
- **Dado** um leitor com 3 empréstimos ativos, **quando** tentar emprestar outro, **então** o sistema recusa o empréstimo.
- **Dado** um leitor com empréstimo atrasado, **quando** tentar emprestar, **então** o sistema recusa o empréstimo.
- **Dada** uma devolução com 2 dias de atraso, **quando** registrada, **então** é gerada multa de R$ 2,00.

## Como executar

Pré-requisito: JDK 21 ou superior (o Maven Wrapper baixa o Maven automaticamente).

```bash
./mvnw verify                                   # compila, roda os testes e verifica a cobertura
java -jar target/sistema-biblioteca-0.1.0-SNAPSHOT.jar   # executa a aplicação
```

O relatório de cobertura é gerado em `target/site/jacoco/index.html`.

## Estrutura do repositório

```
.
├── .github/workflows/ci.yml   # pipeline de integração contínua
├── .mvn/wrapper/              # configuração do Maven Wrapper
├── mvnw / mvnw.cmd            # Maven Wrapper (Linux/macOS e Windows)
├── src/main/java/             # código-fonte
├── src/test/java/             # testes unitários
├── pom.xml                    # build Maven, JUnit, JaCoCo, SonarCloud
├── CONTRIBUTING.md            # fluxo de trabalho e convenção de commits
├── LICENSE
└── README.md
```

## Contribuição

Veja [CONTRIBUTING.md](CONTRIBUTING.md) para o fluxo de branches e a convenção das mensagens de commit.

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
