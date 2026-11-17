package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmprestimoServiceTest {

    private static final String ISBN = "9788535914849";
    private static final String CPF = "52998224725";

    private LivroRepositorio livroRepositorio;
    private LeitorRepositorio leitorRepositorio;
    private EmprestimoRepositorio emprestimoRepositorio;
    private EmprestimoService service;
    private Livro livro;
    private Leitor leitor;

    @BeforeEach
    void setUp() {
        livroRepositorio = new LivroRepositorio();
        leitorRepositorio = new LeitorRepositorio();
        emprestimoRepositorio = new EmprestimoRepositorio();
        Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        service = new EmprestimoService(livroRepositorio, leitorRepositorio, emprestimoRepositorio, relogio);

        livro = new Livro(ISBN, "Dom Casmurro", "Machado de Assis", 1899, 2);
        leitor = new Leitor(CPF, "Maria Souza", "maria@exemplo.com");
        livroRepositorio.salvar(livro);
        leitorRepositorio.salvar(leitor);
    }

    @Test
    void deveRegistrarEmprestimoComDevolucaoEmSeteDiasEDiminuirDisponibilidade() {
        Emprestimo emprestimo = service.emprestar(CPF, ISBN);

        LocalDate dataEmprestimo = LocalDate.of(2026, 10, 26);
        assertEquals(livro, emprestimo.getLivro());
        assertEquals(leitor, emprestimo.getLeitor());
        assertEquals(dataEmprestimo, emprestimo.getDataEmprestimo());
        assertEquals(dataEmprestimo.plusDays(7), emprestimo.getDataPrevista());
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertEquals(1, emprestimoRepositorio.listarAtivosPorLeitor(leitor).size());
    }

    @Test
    void deveAceitarCpfEIsbnComFormatacao() {
        Emprestimo emprestimo = service.emprestar("529.982.247-25", "978-85-359-1484-9");

        assertEquals(leitor, emprestimo.getLeitor());
        assertEquals(livro, emprestimo.getLivro());
        assertEquals(1, livro.getQuantidadeDisponivel());
    }

    @Test
    void deveRecusarQuandoNaoHaExemplarDisponivel() {
        livro.retirarExemplar();
        livro.retirarExemplar();

        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> service.emprestar(CPF, ISBN));

        assertEquals("Livro indisponível", erro.getMessage());
        assertEquals(0, livro.getQuantidadeDisponivel());
        assertTrue(emprestimoRepositorio.listarAtivosPorLeitor(leitor).isEmpty());
    }

    @Test
    void deveRecusarQuandoLeitorNaoEstaCadastrado() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.emprestar("11144477735", ISBN));

        assertEquals("Leitor não cadastrado", erro.getMessage());
        assertEquals(2, livro.getQuantidadeDisponivel());
        assertTrue(emprestimoRepositorio.listarTodos().isEmpty());
    }

    @Test
    void deveRecusarQuandoLivroNaoEstaCadastrado() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.emprestar(CPF, "8535914846"));

        assertEquals("Livro não cadastrado", erro.getMessage());
        assertEquals(2, livro.getQuantidadeDisponivel());
        assertTrue(emprestimoRepositorio.listarAtivosPorLeitor(leitor).isEmpty());
    }

    @Test
    void construtorPadraoDeveUsarRelogioDoSistema() {
        EmprestimoService servicePadrao = new EmprestimoService(
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        Emprestimo emprestimo = servicePadrao.emprestar(CPF, ISBN);

        assertEquals(LocalDate.now().plusDays(7), emprestimo.getDataPrevista());
        assertEquals(1, livro.getQuantidadeDisponivel());
    }

    @Test
    void deveRecusarQuandoLeitorJaPossuiTresEmprestimosAtivos() {
        Livro comQuatroExemplares = new Livro(ISBN, "Dom Casmurro", "Machado de Assis", 1899, 4);
        livroRepositorio.salvar(comQuatroExemplares);
        service.emprestar(CPF, ISBN);
        service.emprestar(CPF, ISBN);
        service.emprestar(CPF, ISBN);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> service.emprestar(CPF, ISBN));

        assertEquals("Limite de 3 empréstimos ativos atingido", erro.getMessage());
        assertEquals(1, comQuatroExemplares.getQuantidadeDisponivel());
        assertEquals(3, emprestimoRepositorio.listarAtivosPorLeitor(leitor).size());
    }

    @Test
    void deveRecusarQuandoLeitorPossuiEmprestimoAtrasado() {
        Clock dataDoEmprestimo = Clock.fixed(Instant.parse("2026-10-10T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        EmprestimoService serviceAntigo = new EmprestimoService(
                livroRepositorio, leitorRepositorio, emprestimoRepositorio, dataDoEmprestimo);
        serviceAntigo.emprestar(CPF, ISBN);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> service.emprestar(CPF, ISBN));

        assertEquals("Leitor possui empréstimo em atraso", erro.getMessage());
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertEquals(1, emprestimoRepositorio.listarAtivosPorLeitor(leitor).size());
    }

    @Test
    void devePermitirNovoEmprestimoNoDiaDaDevolucaoPrevista() {
        Clock dataDoEmprestimo = Clock.fixed(Instant.parse("2026-10-19T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        EmprestimoService serviceNoPrazo = new EmprestimoService(
                livroRepositorio, leitorRepositorio, emprestimoRepositorio, dataDoEmprestimo);
        serviceNoPrazo.emprestar(CPF, ISBN);

        Emprestimo segundo = service.emprestar(CPF, ISBN);

        assertEquals(LocalDate.of(2026, 11, 2), segundo.getDataPrevista());
        assertEquals(0, livro.getQuantidadeDisponivel());
        assertEquals(2, emprestimoRepositorio.listarAtivosPorLeitor(leitor).size());
    }
}
