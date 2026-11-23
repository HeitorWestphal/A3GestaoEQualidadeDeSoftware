package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Cpf;
import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Isbn;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.math.BigDecimal;
import java.time.Clock;

/**
 * Registro de devolução e cálculo de multa por atraso (RF05, RN06).
 */
public class DevolucaoService {

    static final BigDecimal MULTA_POR_DIA = new BigDecimal("1.00");

    private final LivroRepositorio livroRepositorio;
    private final LeitorRepositorio leitorRepositorio;
    private final EmprestimoRepositorio emprestimoRepositorio;
    private final Clock relogio;

    public DevolucaoService(LivroRepositorio livroRepositorio,
                            LeitorRepositorio leitorRepositorio,
                            EmprestimoRepositorio emprestimoRepositorio) {
        this(livroRepositorio, leitorRepositorio, emprestimoRepositorio, Clock.systemDefaultZone());
    }

    DevolucaoService(LivroRepositorio livroRepositorio,
                     LeitorRepositorio leitorRepositorio,
                     EmprestimoRepositorio emprestimoRepositorio,
                     Clock relogio) {
        this.livroRepositorio = livroRepositorio;
        this.leitorRepositorio = leitorRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.relogio = relogio;
    }

    public BigDecimal calcularMulta(Emprestimo emprestimo) {
        long diasAtraso = emprestimo.calcularDiasAtraso(relogio);
        return MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
    }

    public BigDecimal devolver(Emprestimo emprestimo) {
        if (emprestimo == null) {
            throw new RegraNegocioException("Empréstimo inexistente");
        }
        if (!emprestimo.isAtivo()) {
            throw new RegraNegocioException("Empréstimo já encerrado");
        }
        emprestimo.registrarDevolucao(relogio);
        emprestimo.getLivro().devolverExemplar();
        return calcularMulta(emprestimo);
    }

    public BigDecimal devolver(String cpf, String isbn) {
        Leitor leitor = leitorRepositorio.buscarPorCpf(Cpf.normalizar(cpf))
                .orElseThrow(() -> new RegraNegocioException("Leitor não cadastrado"));
        Livro livro = livroRepositorio.buscarPorIsbn(Isbn.normalizar(isbn))
                .orElseThrow(() -> new RegraNegocioException("Livro não cadastrado"));

        java.util.Optional<Emprestimo> ativo = emprestimoRepositorio.buscarAtivoPorLeitorELivro(leitor, livro);
        if (ativo.isPresent()) {
            return devolver(ativo.get());
        }

        if (emprestimoRepositorio.buscarUltimoPorLeitorELivro(leitor, livro).isPresent()) {
            throw new RegraNegocioException("Empréstimo já encerrado");
        }

        throw new RegraNegocioException("Empréstimo inexistente");
    }
}
