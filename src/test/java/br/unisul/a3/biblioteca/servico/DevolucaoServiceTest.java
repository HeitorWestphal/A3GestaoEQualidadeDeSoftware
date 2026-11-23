package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class DevolucaoServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final String ISBN = "9788535914849";
    private static final String CPF = "52998224725";

    private final Livro livro = new Livro(ISBN, "Dom Casmurro", "Machado de Assis", 1899, 2);
    private final Leitor leitor = new Leitor(CPF, "Maria Souza", "maria@exemplo.com");

    @Test
    void deveRetornarMultaZeroQuandoDevolvidoNoPrazo() {
        Clock diaDoEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        Clock noPrazo = Clock.fixed(Instant.parse("2026-11-01T12:00:00Z"), ZONA);

        livro.retirarExemplar();
        Emprestimo emprestimo = new Emprestimo(livro, leitor, diaDoEmprestimo);
        emprestimo.registrarDevolucao(noPrazo);

        DevolucaoService service = criarService(noPrazo);
        BigDecimal multa = service.calcularMulta(emprestimo);

        assertEquals(new BigDecimal("0.00"), multa);
    }

    @Test
    void deveRetornarMultaDeDoisReaisQuandoDevolveDoisDiasAtrasado() {
        Clock diaDoEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        // Prevista: 02/11. Devolução: 04/11 -> 2 dias de atraso
        Clock doisDiasAposPrazo = Clock.fixed(Instant.parse("2026-11-04T12:00:00Z"), ZONA);

        livro.retirarExemplar();
        Emprestimo emprestimo = new Emprestimo(livro, leitor, diaDoEmprestimo);
        emprestimo.registrarDevolucao(doisDiasAposPrazo);

        DevolucaoService service = criarService(doisDiasAposPrazo);
        BigDecimal multa = service.calcularMulta(emprestimo);

        assertEquals(new BigDecimal("2.00"), multa);
    }

    @Test
    void deveRetornarMultaPrevistaParaEmprestimoAindaAtivo() {
        Clock diaDoEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        // Prevista: 02/11. "Hoje": 05/11 -> 3 dias de atraso
        Clock tresDiasAposPrazo = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZONA);

        livro.retirarExemplar();
        Emprestimo emprestimo = new Emprestimo(livro, leitor, diaDoEmprestimo);

        DevolucaoService service = criarService(tresDiasAposPrazo);
        BigDecimal multa = service.calcularMulta(emprestimo);

        assertEquals(new BigDecimal("3.00"), multa);
    }

    @Test
    void deveDevolverEmprestimoEAumentarDisponibilidade() {
        Clock diaDoEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        Clock noPrazo = Clock.fixed(Instant.parse("2026-11-01T12:00:00Z"), ZONA);

        LivroRepositorio livroRepo = new LivroRepositorio();
        LeitorRepositorio leitorRepo = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio();
        LivroService livroService = new LivroService(livroRepo, diaDoEmprestimo);
        LeitorService leitorService = new LeitorService(leitorRepo);
        EmprestimoService emprestimoService = new EmprestimoService(
                livroRepo, leitorRepo, emprestimoRepo, diaDoEmprestimo);

        Livro livroCadastrado = livroService.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, 1899, 2);
        leitorService.cadastrar("Maria Souza", CPF, "maria@exemplo.com");
        emprestimoService.emprestar(CPF, ISBN);

        assertEquals(1, livroCadastrado.getQuantidadeDisponivel());

        DevolucaoService devolucaoService = new DevolucaoService(livroRepo, leitorRepo, emprestimoRepo, noPrazo);
        BigDecimal multa = devolucaoService.devolver(CPF, ISBN);

        assertEquals(new BigDecimal("0.00"), multa);
        assertEquals(2, livroCadastrado.getQuantidadeDisponivel());
        assertTrue(livroCadastrado.isDisponivel());
        assertFalse(emprestimoRepo.listarAtivosPorLeitor(leitor).isEmpty() 
                && emprestimoRepo.listarAtivosPorLeitor(leitor).stream().anyMatch(Emprestimo::isAtivo));
    }

    @Test
    void deveRecusarDevolverEmprestimoInexistente() {
        LivroRepositorio livroRepo = new LivroRepositorio();
        LeitorRepositorio leitorRepo = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio();

        livroRepo.salvar(livro);
        leitorRepo.salvar(leitor);

        DevolucaoService service = new DevolucaoService(livroRepo, leitorRepo, emprestimoRepo, Clock.systemDefaultZone());

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.devolver(CPF, ISBN));
        assertEquals("Empréstimo inexistente", erro.getMessage());
    }

    @Test
    void deveRecusarDevolverEmprestimoJaEncerradoPorCpfEIsbn() {
        Clock diaDoEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        LivroRepositorio livroRepo = new LivroRepositorio();
        LeitorRepositorio leitorRepo = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio();

        Livro livroCadastrado = new Livro(ISBN, "Dom Casmurro", "Machado de Assis", 1899, 2);
        livroCadastrado.retirarExemplar();
        livroRepo.salvar(livroCadastrado);
        leitorRepo.salvar(leitor);

        Emprestimo emprestimo = new Emprestimo(livroCadastrado, leitor, diaDoEmprestimo);
        emprestimoRepo.salvar(emprestimo);

        DevolucaoService service = new DevolucaoService(livroRepo, leitorRepo, emprestimoRepo, diaDoEmprestimo);
        service.devolver(CPF, ISBN);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.devolver(CPF, ISBN));
        assertEquals("Empréstimo já encerrado", erro.getMessage());
    }

    @Test
    void deveRecusarDevolverEmprestimoNull() {
        DevolucaoService service = criarService(Clock.systemDefaultZone());

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.devolver((Emprestimo) null));
        assertEquals("Empréstimo inexistente", erro.getMessage());
    }

    @Test
    void deveRecusarDevolverObjetoEmprestimoJaEncerrado() {
        Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        DevolucaoService service = criarService(relogio);

        livro.retirarExemplar();
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        service.devolver(emprestimo);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.devolver(emprestimo));
        assertEquals("Empréstimo já encerrado", erro.getMessage());
    }

    @Test
    void deveRecusarDevolverQuandoLeitorNaoCadastrado() {
        LivroRepositorio livroRepo = new LivroRepositorio();
        livroRepo.salvar(livro);
        DevolucaoService service = new DevolucaoService(livroRepo, new LeitorRepositorio(),
                new EmprestimoRepositorio(), Clock.systemDefaultZone());

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.devolver(CPF, ISBN));
        assertEquals("Leitor não cadastrado", erro.getMessage());
    }

    @Test
    void deveRecusarDevolverQuandoLivroNaoCadastrado() {
        LeitorRepositorio leitorRepo = new LeitorRepositorio();
        leitorRepo.salvar(leitor);
        DevolucaoService service = new DevolucaoService(new LivroRepositorio(), leitorRepo,
                new EmprestimoRepositorio(), Clock.systemDefaultZone());

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.devolver(CPF, ISBN));
        assertEquals("Livro não cadastrado", erro.getMessage());
    }

    private DevolucaoService criarService(Clock relogio) {
        return new DevolucaoService(new LivroRepositorio(), new LeitorRepositorio(),
                new EmprestimoRepositorio(), relogio);
    }
}
