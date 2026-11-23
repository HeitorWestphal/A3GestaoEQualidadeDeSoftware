package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

class DevolucaoIntegracaoTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final String ISBN1 = "9788535914849";
    private static final String ISBN2 = "8535914846";
    private static final String CPF = "52998224725";

    @Test
    void deveBloquearEmprestimoPorAtrasoELiberarAposDevolucaoComMulta() {
        Clock dataEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        Clock doisDiasAposPrazo = Clock.fixed(Instant.parse("2026-11-04T12:00:00Z"), ZONA);

        LivroRepositorio livroRepo = new LivroRepositorio();
        LeitorRepositorio leitorRepo = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio();

        LivroService livroService = new LivroService(livroRepo, dataEmprestimo);
        LeitorService leitorService = new LeitorService(leitorRepo);
        EmprestimoService emprestimoServiceInicial = new EmprestimoService(
                livroRepo, leitorRepo, emprestimoRepo, dataEmprestimo);

        Livro livro1 = livroService.cadastrar("Dom Casmurro", "Machado de Assis", ISBN1, 1899, 1);
        Livro livro2 = livroService.cadastrar("Memórias Póstumas", "Machado de Assis", ISBN2, 1881, 1);
        Leitor leitor = leitorService.cadastrar("Maria Souza", CPF, "maria@exemplo.com");

        Emprestimo primeiroEmprestimo = emprestimoServiceInicial.emprestar(CPF, ISBN1);
        assertEquals(0, livro1.getQuantidadeDisponivel());
        assertTrue(primeiroEmprestimo.isAtivo());

        // Com 2 dias de atraso, leitor não pode fazer novo empréstimo (RN05)
        EmprestimoService emprestimoServiceAtrasado = new EmprestimoService(
                livroRepo, leitorRepo, emprestimoRepo, doisDiasAposPrazo);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> emprestimoServiceAtrasado.emprestar(CPF, ISBN2));
        assertEquals("Leitor possui empréstimo em atraso", erro.getMessage());

        // Realiza devolução do livro em atraso (RN06, RF05)
        DevolucaoService devolucaoService = new DevolucaoService(
                livroRepo, leitorRepo, emprestimoRepo, doisDiasAposPrazo);
        BigDecimal multa = devolucaoService.devolver(CPF, ISBN1);

        assertEquals(new BigDecimal("2.00"), multa);
        assertEquals(1, livro1.getQuantidadeDisponivel());
        assertTrue(livro1.isDisponivel());
        assertFalse(primeiroEmprestimo.isAtivo());

        // Após a devolução, o leitor não possui mais pendência e pode emprestar normalmente
        Emprestimo novoEmprestimo = emprestimoServiceAtrasado.emprestar(CPF, ISBN2);

        assertNotNull(novoEmprestimo);
        assertEquals(leitor, novoEmprestimo.getLeitor());
        assertEquals(livro2, novoEmprestimo.getLivro());
        assertEquals(0, livro2.getQuantidadeDisponivel());
        assertTrue(novoEmprestimo.isAtivo());
    }
}
