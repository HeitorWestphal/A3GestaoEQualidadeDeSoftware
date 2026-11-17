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
import java.time.Clock;

/**
 * Registro de empréstimo de exemplares (RF04, RN02).
 */
public class EmprestimoService {

    private final LivroRepositorio livroRepositorio;
    private final LeitorRepositorio leitorRepositorio;
    private final EmprestimoRepositorio emprestimoRepositorio;
    private final Clock relogio;

    public EmprestimoService(LivroRepositorio livroRepositorio,
                             LeitorRepositorio leitorRepositorio,
                             EmprestimoRepositorio emprestimoRepositorio) {
        this(livroRepositorio, leitorRepositorio, emprestimoRepositorio, Clock.systemDefaultZone());
    }

    EmprestimoService(LivroRepositorio livroRepositorio,
                      LeitorRepositorio leitorRepositorio,
                      EmprestimoRepositorio emprestimoRepositorio,
                      Clock relogio) {
        this.livroRepositorio = livroRepositorio;
        this.leitorRepositorio = leitorRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.relogio = relogio;
    }

    public Emprestimo emprestar(String cpf, String isbn) {
        Leitor leitor = leitorRepositorio.buscarPorCpf(Cpf.normalizar(cpf))
                .orElseThrow(() -> new RegraNegocioException("Leitor não cadastrado"));
        Livro livro = livroRepositorio.buscarPorIsbn(Isbn.normalizar(isbn))
                .orElseThrow(() -> new RegraNegocioException("Livro não cadastrado"));

        if (!livro.isDisponivel()) {
            throw new RegraNegocioException("Livro indisponível");
        }

        livro.retirarExemplar();
        Emprestimo emprestimo = new Emprestimo(livro, leitor, relogio);
        return emprestimoRepositorio.salvar(emprestimo);
    }
}
