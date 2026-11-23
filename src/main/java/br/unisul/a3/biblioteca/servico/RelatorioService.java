package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
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
}
