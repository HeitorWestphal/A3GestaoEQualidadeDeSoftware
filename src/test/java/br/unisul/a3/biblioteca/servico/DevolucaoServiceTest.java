package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class DevolucaoServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private final Livro livro = new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2);
    private final Leitor leitor = new Leitor("52998224725", "Maria Souza", "maria@exemplo.com");

    @Test
    void deveRetornarMultaZeroQuandoDevolvidoNoPrazo() {
        Clock diaDoEmprestimo = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZONA);
        Clock noPrazo = Clock.fixed(Instant.parse("2026-11-01T12:00:00Z"), ZONA);

        livro.retirarExemplar();
        Emprestimo emprestimo = new Emprestimo(livro, leitor, diaDoEmprestimo);
        emprestimo.registrarDevolucao(noPrazo);

        DevolucaoService service = new DevolucaoService(noPrazo);
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

        DevolucaoService service = new DevolucaoService(doisDiasAposPrazo);
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

        DevolucaoService service = new DevolucaoService(tresDiasAposPrazo);
        BigDecimal multa = service.calcularMulta(emprestimo);

        assertEquals(new BigDecimal("3.00"), multa);
    }
}
