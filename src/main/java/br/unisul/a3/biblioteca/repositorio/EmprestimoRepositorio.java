package br.unisul.a3.biblioteca.repositorio;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Armazena os empréstimos em memória e consulta os ativos de cada leitor.
 */
public class EmprestimoRepositorio {

    private final List<Emprestimo> emprestimos = new ArrayList<>();

    public Emprestimo salvar(Emprestimo emprestimo) {
        emprestimos.add(emprestimo);
        return emprestimo;
    }

    public List<Emprestimo> listarAtivosPorLeitor(Leitor leitor) {
        List<Emprestimo> ativos = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLeitor().equals(leitor) && emprestimo.isAtivo()) {
                ativos.add(emprestimo);
            }
        }
        return ativos;
    }

    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(emprestimos);
    }

    public Optional<Emprestimo> buscarAtivoPorLeitorELivro(Leitor leitor, Livro livro) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLeitor().equals(leitor)
                    && emprestimo.getLivro().equals(livro)
                    && emprestimo.isAtivo()) {
                return Optional.of(emprestimo);
            }
        }
        return Optional.empty();
    }
}
