package br.unisul.a3.biblioteca.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Leitor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeitorRepositorioTest {

    private LeitorRepositorio repositorio;
    private Leitor maria;

    @BeforeEach
    void setUp() {
        repositorio = new LeitorRepositorio();
        maria = new Leitor("52998224725", "Maria Souza", "maria@exemplo.com");
    }

    @Test
    void deveSalvarEBuscarPorCpf() {
        repositorio.salvar(maria);

        assertTrue(repositorio.existe("52998224725"));
        assertEquals(maria, repositorio.buscarPorCpf("52998224725").orElseThrow());
    }

    @Test
    void deveRetornarVazioQuandoCpfNaoCadastrado() {
        assertFalse(repositorio.existe("11144477735"));
        assertTrue(repositorio.buscarPorCpf("11144477735").isEmpty());
    }

    @Test
    void deveListarLeitoresNaOrdemDeCadastro() {
        Leitor joao = new Leitor("11144477735", "João Lima", "joao@exemplo.com");
        repositorio.salvar(maria);
        repositorio.salvar(joao);

        assertEquals(List.of(maria, joao), repositorio.listarTodos());
    }

    @Test
    void listaRetornadaNaoDeveAlterarORepositorio() {
        repositorio.salvar(maria);
        repositorio.listarTodos().clear();

        assertEquals(1, repositorio.listarTodos().size());
    }
}
