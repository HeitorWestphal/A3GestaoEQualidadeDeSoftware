package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.excecao.RegraNegocioException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class EmprestimoTest {

    private final Livro livro = new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2);
    private final Leitor leitor = new Leitor("52998224725", "Maria Souza", "maria@exemplo.com");
    private final Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    @Test
    void dataPrevistaDeveSerSeteDiasAposOEmprestimo() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);

        LocalDate dataEmprestimo = LocalDate.of(2026, 10, 26);
        assertEquals(livro, emprestimo.getLivro());
        assertEquals(leitor, emprestimo.getLeitor());
        assertEquals(dataEmprestimo, emprestimo.getDataEmprestimo());
        assertEquals(dataEmprestimo.plusDays(Emprestimo.PRAZO_EM_DIAS), emprestimo.getDataPrevista());
        assertNull(emprestimo.getDataDevolucao());
        assertTrue(emprestimo.isAtivo());
    }

    @Test
    void naoDeveEstarAtrasadoQuandoAindaEstaDentroDoPrazo() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);

        assertFalse(emprestimo.isAtrasado(relogio));
    }

    @Test
    void deveEstarAtrasadoQuandoADataAtualUltrapassaAPrevista() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        Clock depoisDoPrazo = Clock.fixed(Instant.parse("2026-11-03T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

        assertTrue(emprestimo.isAtrasado(depoisDoPrazo));
    }

    @Test
    void deveRegistrarDevolucaoNoPrazoSemAtraso() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        Clock noPrazo = Clock.fixed(Instant.parse("2026-11-01T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

        emprestimo.registrarDevolucao(noPrazo);

        assertEquals(LocalDate.of(2026, 11, 1), emprestimo.getDataDevolucao());
        assertFalse(emprestimo.isAtivo());
        assertEquals(0, emprestimo.calcularDiasAtraso(noPrazo));
    }

    @Test
    void deveCalcularDiasDeAtrasoQuandoDevolvidoAposOPrazo() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        Clock doisDiasAposPrazo = Clock.fixed(Instant.parse("2026-11-04T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

        emprestimo.registrarDevolucao(doisDiasAposPrazo);

        assertEquals(LocalDate.of(2026, 11, 4), emprestimo.getDataDevolucao());
        assertFalse(emprestimo.isAtivo());
        assertEquals(2, emprestimo.calcularDiasAtraso(doisDiasAposPrazo));
    }

    @Test
    void deveRecusarDevolucaoDeEmprestimoJaDevolvido() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        emprestimo.registrarDevolucao(relogio);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> emprestimo.registrarDevolucao(relogio));

        assertEquals("Empréstimo já encerrado", erro.getMessage());
    }

    @Test
    void deveCalcularDiasDeAtrasoDeEmprestimoAtivo() {
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        Clock doisDiasAposPrazo = Clock.fixed(Instant.parse("2026-11-04T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

        assertTrue(emprestimo.isAtivo());
        assertEquals(2, emprestimo.calcularDiasAtraso(doisDiasAposPrazo));
    }
}
