package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

/**
 * Consultas de empréstimos em atraso e histórico do leitor (RF06).
 */
public class RelatorioService {

    private final EmprestimoRepositorio emprestimoRepositorio;
    private final Clock relogio;

    public RelatorioService(EmprestimoRepositorio emprestimoRepositorio) {
        this(emprestimoRepositorio, Clock.systemDefaultZone());
    }

    RelatorioService(EmprestimoRepositorio emprestimoRepositorio, Clock relogio) {
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.relogio = relogio;
    }

    public List<Emprestimo> listarAtivos() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(Emprestimo::isAtivo)
                .toList();
    }

    public List<Emprestimo> listarAtrasados() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(emprestimo -> emprestimo.isAtrasado(relogio))
                .toList();
    }

    /**
     * Empréstimos em atraso com os dias de atraso e a multa prevista até hoje,
     * usando a mesma regra da devolução (RN06).
     */
    public List<EmprestimoAtrasado> listarAtrasosComMulta() {
        return listarAtrasados().stream()
                .map(this::calcularAtraso)
                .toList();
    }

    private EmprestimoAtrasado calcularAtraso(Emprestimo emprestimo) {
        long diasAtraso = emprestimo.calcularDiasAtraso(relogio);
        BigDecimal multaPrevista = DevolucaoService.MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
        return new EmprestimoAtrasado(emprestimo, diasAtraso, multaPrevista);
    }
}
