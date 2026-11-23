package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import java.math.BigDecimal;

/**
 * Linha do relatório de atrasos: o empréstimo, os dias de atraso e a multa prevista (RF06, RN06).
 */
public record EmprestimoAtrasado(Emprestimo emprestimo, long diasAtraso, BigDecimal multaPrevista) {
}
