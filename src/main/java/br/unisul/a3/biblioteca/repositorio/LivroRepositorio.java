package br.unisul.a3.biblioteca.repositorio;

import br.unisul.a3.biblioteca.dominio.Livro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Armazena os livros em memória, indexados pelo ISBN.
 */
public class LivroRepositorio {

    private final Map<String, Livro> livros = new LinkedHashMap<>();

    public Livro salvar(Livro livro) {
        livros.put(livro.getIsbn(), livro);
        return livro;
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return Optional.ofNullable(livros.get(isbn));
    }

    public boolean existe(String isbn) {
        return livros.containsKey(isbn);
    }

    public List<Livro> listarTodos() {
        return new ArrayList<>(livros.values());
    }
}
