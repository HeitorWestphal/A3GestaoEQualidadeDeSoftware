package br.unisul.a3.biblioteca.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Livro;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LivroRepositorioTest {

    private LivroRepositorio repositorio;
    private Livro domCasmurro;

    @BeforeEach
    void setUp() {
        repositorio = new LivroRepositorio();
        domCasmurro = new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2);
    }

    @Test
    void deveSalvarEBuscarPorIsbn() {
        repositorio.salvar(domCasmurro);

        assertTrue(repositorio.existe("9788535914849"));
        assertEquals(domCasmurro, repositorio.buscarPorIsbn("9788535914849").orElseThrow());
    }

    @Test
    void deveRetornarVazioQuandoIsbnNaoCadastrado() {
        assertFalse(repositorio.existe("8535914846"));
        assertTrue(repositorio.buscarPorIsbn("8535914846").isEmpty());
    }

    @Test
    void deveListarLivrosNaOrdemDeCadastro() {
        Livro outro = new Livro("8535914846", "Memórias Póstumas", "Machado de Assis", 1881, 1);
        repositorio.salvar(domCasmurro);
        repositorio.salvar(outro);

        assertEquals(List.of(domCasmurro, outro), repositorio.listarTodos());
    }

    @Test
    void listaRetornadaNaoDeveAlterarORepositorio() {
        repositorio.salvar(domCasmurro);
        repositorio.listarTodos().clear();

        assertEquals(1, repositorio.listarTodos().size());
    }
}
