package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
