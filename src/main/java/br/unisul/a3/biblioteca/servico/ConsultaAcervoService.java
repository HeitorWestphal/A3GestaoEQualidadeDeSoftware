package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Isbn;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
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
}
