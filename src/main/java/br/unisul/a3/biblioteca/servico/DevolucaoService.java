package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import java.math.BigDecimal;
import java.time.Clock;

/**
 * Registro de devolução e cálculo de multa por atraso (RF05, RN06).
 */
public class DevolucaoService {

    static final BigDecimal MULTA_POR_DIA = new BigDecimal("1.00");

    private final Clock relogio;

    public DevolucaoService() {
        this(Clock.systemDefaultZone());
    }

    DevolucaoService(Clock relogio) {
        this.relogio = relogio;
    }

    public BigDecimal calcularMulta(Emprestimo emprestimo) {
        long diasAtraso = emprestimo.calcularDiasAtraso(relogio);
        return MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
    }
}
