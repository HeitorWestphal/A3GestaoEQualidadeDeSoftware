package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Cpf;
import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Todos os empréstimos do leitor, ativos e devolvidos, do mais recente para o mais antigo.
     */
    public List<Emprestimo> listarHistoricoDoLeitor(String cpf) {
        String cpfNormalizado = Cpf.normalizar(cpf);
        List<Emprestimo> historico = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimoRepositorio.listarTodos()) {
            if (emprestimo.getLeitor().getCpf().equals(cpfNormalizado)) {
                historico.add(emprestimo);
            }
        }
        // Inverte a ordem de cadastro para que, na mesma data, o último registrado venha primeiro.
        Collections.reverse(historico);
        historico.sort(Comparator.comparing(Emprestimo::getDataEmprestimo).reversed());
        return historico;
    }

    private EmprestimoAtrasado calcularAtraso(Emprestimo emprestimo) {
        long diasAtraso = emprestimo.calcularDiasAtraso(relogio);
        BigDecimal multaPrevista = DevolucaoService.MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
        return new EmprestimoAtrasado(emprestimo, diasAtraso, multaPrevista);
    }
}
