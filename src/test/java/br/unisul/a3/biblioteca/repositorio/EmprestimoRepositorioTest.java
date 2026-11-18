package br.unisul.a3.biblioteca.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmprestimoRepositorioTest {

    private final Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
    private final Livro domCasmurro = new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2);
    private final Livro memorias = new Livro("8535914846", "Memórias Póstumas", "Machado de Assis", 1881, 1);
    private final Leitor maria = new Leitor("52998224725", "Maria Souza", "maria@exemplo.com");
    private final Leitor joao = new Leitor("11144477735", "João Lima", "joao@exemplo.com");

    private EmprestimoRepositorio repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new EmprestimoRepositorio();
    }

    @Test
    void deveSalvarEListarEmprestimosAtivosDoLeitor() {
        Emprestimo primeiro = new Emprestimo(domCasmurro, maria, relogio);
        Emprestimo segundo = new Emprestimo(memorias, maria, relogio);
        repositorio.salvar(primeiro);
        repositorio.salvar(segundo);

        assertEquals(List.of(primeiro, segundo), repositorio.listarAtivosPorLeitor(maria));
        assertEquals(List.of(primeiro, segundo), repositorio.listarTodos());
    }

    @Test
    void naoDeveIncluirEmprestimoDeOutroLeitorNaListaDeAtivos() {
        Emprestimo daMaria = new Emprestimo(domCasmurro, maria, relogio);
        Emprestimo doJoao = new Emprestimo(memorias, joao, relogio);
        repositorio.salvar(daMaria);
        repositorio.salvar(doJoao);

        assertEquals(List.of(daMaria), repositorio.listarAtivosPorLeitor(maria));
        assertEquals(List.of(doJoao), repositorio.listarAtivosPorLeitor(joao));
    }

    @Test
    void deveRetornarListaVaziaQuandoLeitorNaoPossuiEmprestimo() {
        assertTrue(repositorio.listarAtivosPorLeitor(maria).isEmpty());
        assertTrue(repositorio.listarTodos().isEmpty());
    }

    @Test
    void listaRetornadaNaoDeveAlterarORepositorio() {
        repositorio.salvar(new Emprestimo(domCasmurro, maria, relogio));
        repositorio.listarTodos().clear();
        repositorio.listarAtivosPorLeitor(maria).clear();

        assertEquals(1, repositorio.listarTodos().size());
        assertEquals(1, repositorio.listarAtivosPorLeitor(maria).size());
    }
}
