package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Cpf;
import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
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

    private final LeitorRepositorio leitorRepositorio;
    private final EmprestimoRepositorio emprestimoRepositorio;
    private final Clock relogio;

    public RelatorioService(LeitorRepositorio leitorRepositorio, EmprestimoRepositorio emprestimoRepositorio) {
        this(leitorRepositorio, emprestimoRepositorio, Clock.systemDefaultZone());
    }

    RelatorioService(LeitorRepositorio leitorRepositorio, EmprestimoRepositorio emprestimoRepositorio,
                     Clock relogio) {
        this.leitorRepositorio = leitorRepositorio;
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
     * Leitor sem empréstimos retorna lista vazia.
     *
     * @throws RegraNegocioException se não houver leitor cadastrado com o CPF informado
     */
    public List<Emprestimo> listarHistoricoDoLeitor(String cpf) {
        Leitor leitor = leitorRepositorio.buscarPorCpf(Cpf.normalizar(cpf))
                .orElseThrow(() -> new RegraNegocioException("Leitor não cadastrado"));
        List<Emprestimo> historico = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimoRepositorio.listarTodos()) {
            if (emprestimo.getLeitor().equals(leitor)) {
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
