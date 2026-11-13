package br.unisul.a3.biblioteca;

import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import br.unisul.a3.biblioteca.servico.ConsultaAcervoService;
import br.unisul.a3.biblioteca.servico.LivroService;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Menu de console do Sistema de Biblioteca.
 */
public final class App {

    static final String NOME_SISTEMA = "Sistema de Biblioteca";

    private final Scanner scanner;
    private final LivroService livroService;
    private final ConsultaAcervoService consultaAcervoService;

    public App(InputStream entrada) {
        scanner = new Scanner(entrada, StandardCharsets.UTF_8);
        LivroRepositorio repositorio = new LivroRepositorio();
        livroService = new LivroService(repositorio);
        consultaAcervoService = new ConsultaAcervoService(repositorio);
    }

    public static void main(String[] args) {
        new App(System.in).executar();
    }

    public void executar() {
        System.out.println(mensagemBoasVindas());
        String opcao;
        while ((opcao = lerLinha(
                "\n1 - Cadastrar livro\n2 - Consultar acervo\n0 - Sair\nEscolha uma opção: ")) != null) {
            switch (opcao) {
                case "1" -> cadastrarLivro();
                case "2" -> consultarAcervo();
                case "0" -> {
                    System.out.println("Encerrando o sistema.");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    static String mensagemBoasVindas() {
        return "Bem-vindo ao " + NOME_SISTEMA + "!";
    }

    private void cadastrarLivro() {
        System.out.println("\nCadastro de livro");
        String titulo = lerLinha("Título: ");
        String autor = lerLinha("Autor: ");
        String isbn = lerLinha("ISBN: ");
        Integer ano = lerInteiro("Ano: ");
        Integer quantidade = lerInteiro("Quantidade de exemplares: ");
        if (titulo == null || autor == null || isbn == null || ano == null || quantidade == null) {
            return;
        }

        try {
            Livro livro = livroService.cadastrar(titulo, autor, isbn, ano, quantidade);
            System.out.println("Livro cadastrado: " + livro.getTitulo());
        } catch (RegraNegocioException erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }

    private void consultarAcervo() {
        String tipoBusca = lerLinha(
                "\nConsultar por:\n1 - ISBN\n2 - Título\n3 - Autor\n0 - Voltar\nEscolha uma opção: ");
        if (tipoBusca == null || tipoBusca.equals("0")) {
            return;
        }
        if (!tipoBusca.equals("1") && !tipoBusca.equals("2") && !tipoBusca.equals("3")) {
            System.out.println("Opção de consulta inválida.");
            return;
        }

        String termo = lerLinha("Termo de busca: ");
        if (termo == null) {
            return;
        }

        try {
            List<Livro> livros = switch (tipoBusca) {
                case "1" -> consultaAcervoService.buscarPorIsbn(termo).stream().toList();
                case "2" -> consultaAcervoService.buscarPorTitulo(termo);
                case "3" -> consultaAcervoService.buscarPorAutor(termo);
                default -> List.of();
            };
            exibirLivros(livros);
        } catch (RegraNegocioException erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }

    private static void exibirLivros(List<Livro> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
            return;
        }
        for (Livro livro : livros) {
            String disponibilidade = livro.isDisponivel() ? "Disponível" : "Indisponível";
            System.out.println(livro + " - " + disponibilidade);
        }
    }

    private Integer lerInteiro(String prompt) {
        String valor = lerLinha(prompt);
        if (valor == null) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException erro) {
            System.out.println("Informe um número inteiro válido.");
            return null;
        }
    }

    private String lerLinha(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}
