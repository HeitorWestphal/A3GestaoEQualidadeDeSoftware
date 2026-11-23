package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class RelatorioServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    // Emprestado em 26/10 -> data prevista 02/11
    private static final Clock DIA_26_10 = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
    // Emprestado em 01/11 -> data prevista 08/11
    private static final Clock DIA_01_11 = Clock.fixed(Instant.parse("2026-11-01T12:00:00Z"), ZONA);
    private static final Clock DIA_05_11 = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZONA);

    private final Livro domCasmurro = new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2);
    private final Livro memorias = new Livro("9788535911664", "Memórias Póstumas", "Machado de Assis", 1881, 2);
    private final Leitor maria = new Leitor("52998224725", "Maria Souza", "maria@exemplo.com");

    private final EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();

    @Test
    void deveListarSomenteEmprestimosAtivos() {
        Emprestimo ativo = emprestar(domCasmurro, maria, DIA_26_10);
        Emprestimo devolvido = emprestar(memorias, maria, DIA_26_10);
        devolvido.registrarDevolucao(DIA_26_10);

        List<Emprestimo> ativos = criarService(DIA_26_10).listarAtivos();

        assertEquals(List.of(ativo), ativos);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaEmprestimos() {
        assertTrue(criarService(DIA_26_10).listarAtivos().isEmpty());
    }

    @Test
    void deveListarSomenteEmprestimosAtrasados() {
        Emprestimo atrasado = emprestar(domCasmurro, maria, DIA_26_10);
        emprestar(memorias, maria, DIA_01_11);

        List<Emprestimo> atrasados = criarService(DIA_05_11).listarAtrasados();

        assertEquals(List.of(atrasado), atrasados);
    }

    @Test
    void naoDeveListarEmprestimoDevolvidoComoAtivoNemAtrasado() {
        Emprestimo devolvidoComAtraso = emprestar(domCasmurro, maria, DIA_26_10);
        devolvidoComAtraso.registrarDevolucao(DIA_05_11);

        RelatorioService service = criarService(DIA_05_11);

        assertTrue(service.listarAtivos().isEmpty());
        assertTrue(service.listarAtrasados().isEmpty());
    }

    @Test
    void deveCalcularTresDiasDeAtrasoEMultaDeTresReais() {
        Emprestimo atrasado = emprestar(domCasmurro, maria, DIA_26_10);
        emprestar(memorias, maria, DIA_01_11);

        List<EmprestimoAtrasado> atrasos = criarService(DIA_05_11).listarAtrasosComMulta();

        assertEquals(1, atrasos.size());
        EmprestimoAtrasado linha = atrasos.get(0);
        assertEquals(atrasado, linha.emprestimo());
        assertEquals(3, linha.diasAtraso());
        assertEquals(new BigDecimal("3.00"), linha.multaPrevista());
    }

    @Test
    void naoDeveCalcularMultaQuandoNaoHaAtrasos() {
        emprestar(domCasmurro, maria, DIA_01_11);

        assertTrue(criarService(DIA_05_11).listarAtrasosComMulta().isEmpty());
    }

    private Emprestimo emprestar(Livro livro, Leitor leitor, Clock dia) {
        return emprestimoRepositorio.salvar(new Emprestimo(livro, leitor, dia));
    }

    private RelatorioService criarService(Clock hoje) {
        return new RelatorioService(emprestimoRepositorio, hoje);
    }
}
