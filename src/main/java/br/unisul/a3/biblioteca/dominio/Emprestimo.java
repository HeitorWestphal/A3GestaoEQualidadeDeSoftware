package br.unisul.a3.biblioteca.dominio;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Empréstimo de um exemplar a um leitor (RF04, RN04).
 */
public class Emprestimo {

    public static final int PRAZO_EM_DIAS = 7;

    private final Livro livro;
    private final Leitor leitor;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevista;
    private LocalDate dataDevolucao;

    public Emprestimo(Livro livro, Leitor leitor, Clock relogio) {
        this.livro = livro;
        this.leitor = leitor;
        this.dataEmprestimo = LocalDate.now(relogio);
        this.dataPrevista = dataEmprestimo.plusDays(PRAZO_EM_DIAS);
    }

    public Livro getLivro() {
        return livro;
    }

    public Leitor getLeitor() {
        return leitor;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public boolean isAtivo() {
        return dataDevolucao == null;
    }
}
