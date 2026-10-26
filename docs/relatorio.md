# Relatório final — estrutura e textos-base

> Base do trabalho escrito da A3 (entrega em PDF até 29/11/2026, no modelo de TCC do caderno de metodologia da UNISUL). Os textos daqui são passados para o modelo e formatados lá. O capítulo 2 vem do [plano de testes](plano-de-testes.md).

## Elementos pré-textuais

**Capa / folha de rosto**

- UNIVERSIDADE DO SUL DE SANTA CATARINA – UNISUL
- Unidade Curricular: Gestão e Qualidade de Software. Professor: Jorge Werner
- Integrantes:
  - Heitor Della Barba Westphal (RA 1072410915)
  - João Víctor Muniz da Silva (RA 1072413992)
  - Ian Santiago Silveira (RA 1072410937)
  - João Gabriel Weber dos Santos Verona (RA 10724265816)
  - Bruno Hudson Siebert Costa (RA 1072419878)
- Título: **SISTEMA DE BIBLIOTECA: PLANO E EXECUÇÃO DE TESTES COM INTEGRAÇÃO CONTÍNUA E ANÁLISE ESTÁTICA**
- Cidade, 2026

**Sumário:** gerado automaticamente no modelo.

## 1 Introdução

A transformação digital levou as equipes de desenvolvimento a adotar práticas ágeis e de DevOps, em que integração contínua, automação de testes e análise estática de código garantem que o software seja entregue rápido e com qualidade. Nesse cenário, a qualidade deixa de ser uma etapa final e passa a acompanhar todo o ciclo de desenvolvimento (*shift-left testing*).

Este trabalho aplica essas práticas no desenvolvimento de um **Sistema de Biblioteca**. Bibliotecas pequenas, como as escolares, comunitárias e de empresas, costumam controlar acervo e empréstimos em planilhas ou cadernos. Isso gera perda de livros, empréstimos sem devolução e falta de informação sobre a disponibilidade do acervo. O sistema centraliza o cadastro de livros e leitores e controla empréstimos e devoluções, aplicando regras como limite de empréstimos por leitor, prazo de devolução e multa por atraso.

O **objetivo geral** é planejar e executar os testes do sistema, garantindo pelo menos 75% de cobertura do código-fonte e aprovação no *quality gate* da análise estática. Os **objetivos específicos** são:

1. levantar os requisitos funcionais e de qualidade do sistema;
2. elaborar um plano de testes com testes unitários, de integração e de sistema;
3. automatizar a execução dos testes com integração contínua (GitHub Actions);
4. aplicar análise estática de código (SonarCloud) e corrigir os problemas encontrados;
5. gerenciar a configuração do software com controle de versão no GitHub.

O sistema foi desenvolvido em Java 21, com Maven, JUnit 5 e JaCoCo. O trabalho está organizado da seguinte forma:

- o capítulo 2 apresenta o plano de testes, do objetivo ao controle de versão;
- o capítulo 3 traz as conclusões e lições aprendidas.

## 2 Desenvolvimento (plano de testes)

Conteúdo em [plano-de-testes.md](plano-de-testes.md), seções 2.1 a 2.13. Antes da entrega, completar:

- [ ] 2.9: links finais dos testes de cada integrante (tabela do README).
- [ ] 2.10: cobertura final e resultado do quality gate (print do SonarCloud e do resumo do CI).
- [ ] 2.12: tabela de problemas encontrados e corrigidos, a partir de [qualidade.md](qualidade.md), com análise antes e depois.
- [ ] Prints: execução do CI no GitHub Actions, relatório do JaCoCo, painel do SonarCloud, lista de issues fechadas.

## 3 Conclusão

_Escrever no final do projeto._ Roteiro:

- os objetivos foram atingidos? (cobertura alcançada, quality gate aprovado, issues resolvidas);
- principais problemas encontrados pela análise estática e pelos testes, e como foram corrigidos;
- lições aprendidas sobre testes automatizados, integração contínua e trabalho em equipe com Git;
- trabalhos futuros (persistência em banco de dados, interface gráfica ou web, autenticação).

## Referências

APACHE SOFTWARE FOUNDATION. **Apache Maven**. Disponível em: https://maven.apache.org. Acesso em: 26 out. 2026.

GITHUB. **GitHub Actions documentation**. Disponível em: https://docs.github.com/actions. Acesso em: 26 out. 2026.

JACOCO. **JaCoCo Java Code Coverage Library**. Disponível em: https://www.jacoco.org/jacoco. Acesso em: 26 out. 2026.

JUNIT. **JUnit 5 User Guide**. Disponível em: https://junit.org/junit5/docs/current/user-guide. Acesso em: 26 out. 2026.

PRESSMAN, Roger S.; MAXIM, Bruce R. **Engenharia de software**: uma abordagem profissional. 9. ed. Porto Alegre: AMGH, 2021.

SONARSOURCE. **SonarQube Cloud documentation**. Disponível em: https://docs.sonarsource.com/sonarqube-cloud. Acesso em: 26 out. 2026.

SOMMERVILLE, Ian. **Engenharia de software**. 10. ed. São Paulo: Pearson, 2019.
