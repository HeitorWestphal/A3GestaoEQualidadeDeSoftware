# Plano de testes — Sistema de Biblioteca

> Rascunho de trabalho do capítulo 2 do relatório da A3. Vai sendo atualizado conforme o projeto avança e depois é passado para o modelo de TCC da UNISUL.

## 2.1 Objetivo do plano de testes

Garantir que o Sistema de Biblioteca cumpre os requisitos funcionais (RF01 a RF05) e as regras de negócio (RN01 a RN06) descritos no [README](../README.md), com:

- testes automatizados executados a cada alteração do código (integração contínua);
- cobertura mínima de 75% do código-fonte;
- aprovação no quality gate da análise estática (SonarCloud).

**Justificativa:** o sistema controla empréstimos, prazos e multas. Erros nessas regras causam prejuízo direto para a biblioteca (livros não devolvidos, multas cobradas errado) e para os leitores. Testar desde o início (*shift-left*) evita que defeitos se acumulem enquanto as funcionalidades são feitas em paralelo por cinco pessoas.

## 2.2 Escopo do teste

**Será testado:**

| Módulo | Funcionalidades | Regras |
|--------|-----------------|--------|
| Livros | Cadastro de livro, validação de ISBN | RF01, RN01 |
| Leitores | Cadastro de leitor, validação de CPF e e-mail | RF02 |
| Acervo | Consulta por título, autor e ISBN, disponibilidade | RF03 |
| Empréstimos | Registro de empréstimo, limites e pendências | RF04, RN02 a RN05 |
| Devoluções | Registro de devolução e cálculo de multa | RF05, RN06 |
| Integração | Fluxo completo cadastro → empréstimo → devolução | — |

**Não será testado:**

- **Interface de console (menu):** é uma camada fina que só lê dados e chama os serviços; as regras ficam nos serviços, que são testados. Ela é verificada manualmente na demonstração.
- **Desempenho e carga:** o sistema é de uso local por um bibliotecário, sem acesso concorrente.
- **Persistência em banco de dados:** nesta versão os dados ficam em memória (repositórios). Se um banco for adotado, entra no escopo dos testes de integração.

## 2.3 Contexto do sistema

- **Arquitetura:** aplicação Java em camadas:
  - `dominio`: entidades e validações (Livro, Leitor, Empréstimo, ISBN);
  - `repositorio`: armazenamento em memória;
  - `servico`: regras de negócio;
  - `excecao`: erros de regra de negócio.

  A interface é um menu de console.
- **Tecnologias:** Java 21, Maven (via Maven Wrapper), JUnit 5, JaCoCo, GitHub Actions, SonarCloud.
- **Dependências externas:** nenhuma em tempo de execução. JUnit é usada apenas nos testes.
- **Histórico de mudanças:** registrado nos commits, pull requests e issues do [repositório](https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware). As issues abertas pelo professor (#1 e #2) orientam as prioridades.
- **Documentação existente:** README (requisitos e critérios de aceitação), CONTRIBUTING (fluxo de trabalho), [sonarcloud.md](sonarcloud.md) e [qualidade.md](qualidade.md).

## 2.4 Estratégia de testes

| Tipo | O que cobre | Como | Ferramenta |
|------|-------------|------|------------|
| Unitário | Cada classe de domínio, repositório e serviço isoladamente | Um teste por regra e por caso de erro, com dados fixos; data controlada com `Clock` fixo | JUnit 5 |
| Integração | Serviços trabalhando juntos com os repositórios reais | Cenários do fluxo principal: cadastrar livro e leitor, emprestar, devolver | JUnit 5 |
| Sistema / validação | Critérios de aceitação do README | Cada critério "Dado/Quando/Então" vira um teste automatizado; demonstração do sistema em execução | JUnit 5 + execução manual |
| Regressão | Garantir que nada que funcionava quebrou | Toda a bateria roda em cada push e pull request | GitHub Actions |
| Análise estática | Segurança, confiabilidade, manutenibilidade, duplicação, complexidade | Análise a cada push/PR com quality gate obrigatório | SonarCloud |

**Falta de documentação/conhecimento:** as regras de negócio são escritas no README antes do código, e cada regra tem pelo menos um teste com o mesmo identificador (ex.: RN03). Assim o teste também serve de documentação. Quando uma regra mudar, primeiro é atualizado o README e os critérios de aceitação, depois o teste.

## 2.5 Ambiente de teste

- **Local:** qualquer sistema operacional com JDK 21 ou superior. O Maven Wrapper baixa a versão correta do Maven. Comando: `./mvnw verify`.
- **Integração contínua:** GitHub Actions, `ubuntu-latest`, JDK Temurin 21.
- **Hardware:** sem requisitos especiais (2 GB de RAM livres bastam).
- **Replicação:** o ambiente é reproduzido só com o repositório. Versões do Java, do Maven e de todas as dependências estão fixadas no `pom.xml` e no `.mvn/wrapper`.

## 2.6 Dados de teste

- Os dados são **criados dentro de cada teste** (livros, leitores e empréstimos de exemplo), então os testes não dependem uns dos outros nem de arquivos externos.
- São usados dados realistas: ISBNs e CPFs com dígito verificador válido e títulos de livros conhecidos (ex.: *Dom Casmurro*).
- Datas são controladas por um relógio fixo (`Clock.fixed`), para que testes de prazo e multa deem sempre o mesmo resultado.
- **Dados sensíveis (LGPD):** nenhum dado pessoal real é usado nos testes ou no repositório. CPFs e e-mails são fictícios.

## 2.7 Ferramentas de teste

| Ferramenta | Uso | Justificativa |
|------------|-----|---------------|
| JUnit 5 | Testes unitários e de integração | Padrão em Java, suporte a testes parametrizados, integrado ao Maven |
| JaCoCo | Cobertura de código | Gera relatório HTML/XML e faz o build falhar abaixo de 75% |
| Maven | Build e execução dos testes | Exemplo citado no enunciado; integra com JaCoCo e SonarCloud |
| GitHub Actions | Integração contínua | Executa build, testes e análise a cada push e PR, sem servidor próprio |
| SonarCloud | Análise estática e quality gate | Gratuito para repositório público, mede segurança, confiabilidade, manutenibilidade, duplicação e complexidade |
| GitHub Issues | Gestão de defeitos | Já integrado ao repositório e usado pelo professor |

- **Automação dos testes:** workflow [ci.yml](../.github/workflows/ci.yml), job *Build, testes e cobertura*.
- **Análise estática:** job *Análise estática e quality gate (SonarCloud)*, detalhado em [sonarcloud.md](sonarcloud.md).

## 2.8 Cronograma

| Período | Atividade | Marco |
|---------|-----------|-------|
| 05/10 | Estrutura do projeto, CI, requisitos | Repositório publicado |
| até 31/10 | Cadastros de livro e leitor, consulta, SonarCloud configurado | Primeira análise do quality gate |
| até 07/11 | Empréstimo e devolução com testes | Fluxo principal completo (issue #1) |
| até 14/11 | Testes de integração do fluxo, correção dos problemas do SonarCloud | Quality gate aprovado (issue #2) |
| até 21/11 | Menu de console, revisão da cobertura, relatório | Cobertura ≥ 75% e todas as issues fechadas |
| até 29/11 | Relatório final e slides | Entrega no Ulife |
| 30/11 | Apresentação | — |

**Dependências:** o empréstimo depende dos cadastros de livro e leitor; a devolução depende do empréstimo; o quality gate depende da configuração do SonarCloud.

## 2.9 Responsabilidades

| Integrante | Papel | Responsabilidades |
|------------|-------|-------------------|
| Heitor Della Barba Westphal | Líder de equipe | Organização, revisão dos PRs, cadastro de livro |
| João Víctor Muniz da Silva | Desenvolvedor | Cadastro de leitor |
| Ian Santiago Silveira | Desenvolvedor | Consulta ao acervo e menu da aplicação |
| João Gabriel Weber dos Santos Verona | Testador | Empréstimo e testes de integração do fluxo principal |
| Bruno Hudson Siebert Costa | Especialista em DevOps | Devolução e multa, configuração do SonarCloud e do CI |

Todos são programadores e escrevem os testes unitários da própria funcionalidade. A tabela aluno → funcionalidade → testes fica no [README](../README.md#divisão-das-funcionalidades).

## 2.10 Critérios de aceitação

Os testes são considerados bem-sucedidos quando:

1. todos os testes automatizados passam no CI;
2. a cobertura de linhas é de **pelo menos 75%**: o build falha abaixo disso (JaCoCo);
3. o **quality gate do SonarCloud está aprovado**, conferido pelo CI na própria análise;
4. todas as **issues** do repositório estão resolvidas;
5. cada critério de aceitação do fluxo principal (README) tem um teste automatizado correspondente.

## 2.11 Gestão de defeitos

1. Defeito encontrado (por teste, SonarCloud, colega ou professor) → **issue no GitHub** com descrição, passos para reproduzir, resultado esperado e obtido.
2. A issue é atribuída a um integrante e marcada com rótulo (`bug`, `qualidade`, `professor`).
3. A correção é feita numa branch, com um **teste que reproduz o defeito**, e enviada por pull request que cita a issue (`Closes #N`).
4. O CI precisa passar (testes e quality gate) antes do merge. O merge fecha a issue automaticamente.

## 2.12 Relatórios e métricas

- **Funcionalidades e testes:** tabela no [README](../README.md#divisão-das-funcionalidades).
- **Cobertura:** relatório JaCoCo (artefato `relatorio-jacoco` de cada execução do CI).
- **Análise estática:** resumo de cada execução do job do SonarCloud (gate, cobertura, bugs, vulnerabilidades, code smells, duplicações, complexidade) e histórico com problemas e correções em [qualidade.md](qualidade.md).

## 2.13 Controle de versão

- **Repositório:** <https://github.com/HeitorWestphal/A3GestaoEQualidadeDeSoftware>
- **Branch principal:** `main`. As alterações entram por branches `feat/`, `fix/`, `ci/`, `docs/` e pull requests.
- **Estrutura:** `LICENSE`, `README.md`, `src/main`, `src/test`, `docs/`, `.github/workflows/`.
- **Convenção de commits:** Conventional Commits, descrita em [CONTRIBUTING.md](../CONTRIBUTING.md).
