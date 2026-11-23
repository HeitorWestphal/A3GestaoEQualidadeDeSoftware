# Sistema de Biblioteca

[![CI](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/workflows/ci.yml/badge.svg)](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware/actions/workflows/ci.yml)

Projeto da **A3 – Gestão e Qualidade de Software** (UNISUL, prof. Jorge Werner) — **Tema 4: Sistema de Biblioteca**.

## Integrantes

| Nome | RA | Papel | GitHub |
|------|----|-------|--------|
| Heitor Della Barba Westphal | 1072410915 | Líder de equipe | [@HeitorWestphal](https://github.com/HeitorWestphal) |
| João Víctor Muniz da Silva | 1072413992 | Desenvolvedor | [@Muniz2504](https://github.com/Muniz2504) |
| Ian Santiago Silveira | 1072410937 | Desenvolvedor | [@SantiagoIanS](https://github.com/SantiagoIanS) |
| João Gabriel Weber dos Santos Verona | 10724265816 | Testador | [@1Verona](https://github.com/1Verona) |
| Bruno Hudson Siebert Costa | 1072419878 | Especialista em DevOps | [@BHSC1](https://github.com/BHSC1) |
| João Pedro Nobile dos Santos | 1072411014 | Desenvolvedor | [@jpzyn](https://github.com/jpzyn) |

> Todos os integrantes são programadores e desenvolvem testes unitários das suas funcionalidades.

## Divisão das funcionalidades

Cada integrante implementa e testa a sua funcionalidade e faz os commits com a própria conta do GitHub.

| Aluno | Funcionalidade | Testes unitários |
|-------|----------------|------------------|
| Heitor Della Barba Westphal | RF01 – Cadastrar livro (RN01) | [IsbnTest](src/test/java/br/unisul/a3/biblioteca/dominio/IsbnTest.java), [LivroTest](src/test/java/br/unisul/a3/biblioteca/dominio/LivroTest.java), [LivroRepositorioTest](src/test/java/br/unisul/a3/biblioteca/repositorio/LivroRepositorioTest.java), [LivroServiceTest](src/test/java/br/unisul/a3/biblioteca/servico/LivroServiceTest.java) |
| João Víctor Muniz da Silva | RF02 – Cadastrar leitor | [CpfTest](src/test/java/br/unisul/a3/biblioteca/dominio/CpfTest.java), [EmailTest](src/test/java/br/unisul/a3/biblioteca/dominio/EmailTest.java), [LeitorTest](src/test/java/br/unisul/a3/biblioteca/dominio/LeitorTest.java), [LeitorRepositorioTest](src/test/java/br/unisul/a3/biblioteca/repositorio/LeitorRepositorioTest.java), [LeitorServiceTest](src/test/java/br/unisul/a3/biblioteca/servico/LeitorServiceTest.java)|
| Ian Santiago Silveira | RF03 – Consultar acervo + menu da aplicação | [ConsultaAcervoServiceTest](src/test/java/br/unisul/a3/biblioteca/servico/ConsultaAcervoServiceTest.java), [AppTest](src/test/java/br/unisul/a3/biblioteca/AppTest.java) |
| João Gabriel Weber dos Santos Verona | RF04 – Registrar empréstimo (RN02 a RN05) | [LivroTest](src/test/java/br/unisul/a3/biblioteca/dominio/LivroTest.java), [EmprestimoTest](src/test/java/br/unisul/a3/biblioteca/dominio/EmprestimoTest.java), [EmprestimoRepositorioTest](src/test/java/br/unisul/a3/biblioteca/repositorio/EmprestimoRepositorioTest.java), [EmprestimoServiceTest](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoServiceTest.java), [EmprestimoIntegracaoTest](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoIntegracaoTest.java) |
| Bruno Hudson Siebert Costa | RF05 – Registrar devolução e multa (RN06) + SonarCloud | [LivroTest](src/test/java/br/unisul/a3/biblioteca/dominio/LivroTest.java), [EmprestimoTest](src/test/java/br/unisul/a3/biblioteca/dominio/EmprestimoTest.java), [DevolucaoServiceTest](src/test/java/br/unisul/a3/biblioteca/servico/DevolucaoServiceTest.java), [DevolucaoIntegracaoTest](src/test/java/br/unisul/a3/biblioteca/servico/DevolucaoIntegracaoTest.java) |
| João Pedro Nobile dos Santos | RF06 – Consultar empréstimos em atraso e histórico do leitor | [RelatorioServiceTest](src/test/java/br/unisul/a3/biblioteca/servico/RelatorioServiceTest.java), [AppTest](src/test/java/br/unisul/a3/biblioteca/AppTest.java) |

O planejamento completo dos testes está em [docs/plano-de-testes.md](docs/plano-de-testes.md).

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
- **RF06 – Consultar empréstimos em atraso:** listar empréstimos vencidos com dias de atraso e multa prevista, e o histórico de empréstimos de cada leitor.

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

### Rastreabilidade: critério de aceitação → teste automatizado

Todos os testes de recusa conferem também que **nada foi alterado pela metade**: a disponibilidade do livro e os empréstimos do leitor continuam iguais.

| Critério | Teste |
|----------|-------|
| Empréstimo permitido: devolução em 7 dias e disponibilidade −1 (RN04) | [`EmprestimoServiceTest.deveRegistrarEmprestimoComDevolucaoEmSeteDiasEDiminuirDisponibilidade`](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoServiceTest.java) |
| Livro sem exemplar: recusa com "Livro indisponível", disponibilidade mantida (RN02) | [`EmprestimoServiceTest.deveRecusarQuandoNaoHaExemplarDisponivel`](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoServiceTest.java) |
| Leitor com 3 empréstimos ativos: recusa (RN03) | [`EmprestimoServiceTest.deveRecusarQuandoLeitorJaPossuiTresEmprestimosAtivos`](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoServiceTest.java) |
| Leitor com empréstimo atrasado: recusa (RN05) | [`EmprestimoServiceTest.deveRecusarQuandoLeitorPossuiEmprestimoAtrasado`](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoServiceTest.java) |
| Devolução com 2 dias de atraso: multa de R$ 2,00 (RN06) | [`DevolucaoServiceTest.deveRetornarMultaDeDoisReaisQuandoDevolveDoisDiasAtrasado`](src/test/java/br/unisul/a3/biblioteca/servico/DevolucaoServiceTest.java) |
| Devolução: disponibilidade +1 | [`DevolucaoServiceTest.deveDevolverEmprestimoEAumentarDisponibilidade`](src/test/java/br/unisul/a3/biblioteca/servico/DevolucaoServiceTest.java) |
| Integração: cadastrar livro e leitor → emprestar → consultar disponibilidade | [`EmprestimoIntegracaoTest.deveCadastrarLivroELeitorEmprestarEConsultarDisponibilidade`](src/test/java/br/unisul/a3/biblioteca/servico/EmprestimoIntegracaoTest.java) |
| Integração: atraso bloqueia empréstimo, devolução com multa libera | [`DevolucaoIntegracaoTest.deveBloquearEmprestimoPorAtrasoELiberarAposDevolucaoComMulta`](src/test/java/br/unisul/a3/biblioteca/servico/DevolucaoIntegracaoTest.java) |
| Fluxo completo pelo menu | `AppFluxoCompletoTest` (issue #25) |

## Como executar

Pré-requisito: JDK 21 ou superior (o Maven Wrapper baixa o Maven automaticamente).

```bash
./mvnw verify                                   # compila, roda os testes e verifica a cobertura
java -jar target/sistema-biblioteca-0.1.0-SNAPSHOT.jar   # executa a aplicação
```

A aplicação apresenta um menu de console:

```
1 - Cadastrar livro
2 - Consultar acervo
3 - Relatório de atrasos
4 - Cadastrar leitor
5 - Emprestar livro
6 - Devolver livro
7 - Histórico do leitor
0 - Sair
```

As opções 4 a 7 estão sendo ligadas ao menu nas issues #21 a #24. As regras já estão implementadas e testadas nos serviços.

### Roteiro de demonstração

Dados de exemplo válidos: ISBN `9788535914849`, CPF `529.982.247-25`.

1. **Cadastrar livro** (1): *Dom Casmurro*, Machado de Assis, ISBN `9788535914849`, ano 1899, **1 exemplar**. Repetir o ISBN mostra a recusa "Já existe um livro cadastrado com este ISBN".
2. **Cadastrar leitor** (4): nome, CPF `529.982.247-25` e e-mail. Um CPF inválido, como `111.111.111-11`, é recusado.
3. **Consultar acervo** (2): buscar por "casmurro" mostra `[1/1 disponíveis]`.
4. **Emprestar** (5): CPF e ISBN acima. O empréstimo é registrado com devolução em 7 dias.
5. **Empréstimo recusado** (5): repetir o empréstimo mostra "Livro indisponível". Consultando o acervo de novo aparece `[0/1 disponíveis]`, ou seja, a recusa não alterou nada.
6. **Devolver** (6): sem atraso, não há multa. Consultando o acervo aparece `[1/1 disponíveis]`.
7. **Histórico do leitor** (7) e **relatório de atrasos** (3): mostram os empréstimos do leitor e os atrasados com a multa prevista.

O relatório de cobertura é gerado em `target/site/jacoco/index.html`.

## Estrutura do repositório

```
.
├── .github/workflows/ci.yml   # pipeline de integração contínua
├── .mvn/wrapper/              # configuração do Maven Wrapper
├── mvnw / mvnw.cmd            # Maven Wrapper (Linux/macOS e Windows)
├── .github/scripts/           # script de verificação do quality gate
├── docs/                      # plano de testes, SonarCloud e registro de qualidade
├── src/main/java/             # código-fonte
├── src/test/java/             # testes unitários e de integração
├── pom.xml                    # build Maven, JUnit, JaCoCo, SonarCloud
├── CONTRIBUTING.md            # fluxo de trabalho e convenção de commits
├── LICENSE
└── README.md
```

## Contribuição

Veja [CONTRIBUTING.md](CONTRIBUTING.md) para o fluxo de branches e a convenção das mensagens de commit.

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
