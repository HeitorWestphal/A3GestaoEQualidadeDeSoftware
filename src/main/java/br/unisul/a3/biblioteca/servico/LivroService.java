package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Isbn;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.time.Clock;
import java.time.Year;

/**
 * Cadastro de livros no acervo (RF01, RN01).
 */
public class LivroService {

    static final int ANO_MINIMO = 1450;

    private final LivroRepositorio repositorio;
    private final Clock relogio;

    public LivroService(LivroRepositorio repositorio) {
        this(repositorio, Clock.systemDefaultZone());
    }

    LivroService(LivroRepositorio repositorio, Clock relogio) {
        this.repositorio = repositorio;
        this.relogio = relogio;
    }

    public Livro cadastrar(String titulo, String autor, String isbn, int ano, int quantidade) {
        validarTexto(titulo, "Título");
        validarTexto(autor, "Autor");

        if (!Isbn.isValido(isbn)) {
            throw new RegraNegocioException("ISBN inválido");
        }
        String isbnNormalizado = Isbn.normalizar(isbn);
        if (repositorio.existe(isbnNormalizado)) {
            throw new RegraNegocioException("Já existe um livro cadastrado com este ISBN");
        }

        int anoAtual = Year.now(relogio).getValue();
        if (ano < ANO_MINIMO || ano > anoAtual) {
            throw new RegraNegocioException("Ano deve estar entre " + ANO_MINIMO + " e " + anoAtual);
        }
        if (quantidade < 1) {
            throw new RegraNegocioException("Quantidade de exemplares deve ser pelo menos 1");
        }

        Livro livro = new Livro(isbnNormalizado, titulo.trim(), autor.trim(), ano, quantidade);
        return repositorio.salvar(livro);
    }

    private static void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException(campo + " é obrigatório");
        }
    }
}
