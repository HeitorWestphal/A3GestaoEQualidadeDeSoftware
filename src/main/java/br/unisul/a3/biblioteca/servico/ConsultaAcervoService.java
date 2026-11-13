package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Isbn;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Consultas de livros no acervo.
 */
public class ConsultaAcervoService {

    private final LivroRepositorio repositorio;

    public ConsultaAcervoService(LivroRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return repositorio.buscarPorIsbn(Isbn.normalizar(isbn));
    }

    public List<Livro> buscarPorTitulo(String trecho) {
        String trechoNormalizado = trecho.toLowerCase(Locale.ROOT);
        return repositorio.listarTodos().stream()
                .filter(livro -> livro.getTitulo().toLowerCase(Locale.ROOT).contains(trechoNormalizado))
                .toList();
    }

    public List<Livro> buscarPorAutor(String trecho) {
        String trechoNormalizado = trecho.toLowerCase(Locale.ROOT);
        return repositorio.listarTodos().stream()
                .filter(livro -> livro.getAutor().toLowerCase(Locale.ROOT).contains(trechoNormalizado))
                .toList();
    }
}
