package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class EmprestimoIntegracaoTest {

    private static final String ISBN = "9788535914849";
    private static final String CPF = "52998224725";

    @Test
    void deveCadastrarLivroELeitorEmprestarEConsultarDisponibilidade() {
        Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();

        LivroService livroService = new LivroService(livroRepositorio, relogio);
        LeitorService leitorService = new LeitorService(leitorRepositorio);
        EmprestimoService emprestimoService = new EmprestimoService(
                livroRepositorio, leitorRepositorio, emprestimoRepositorio, relogio);

        Livro livro = livroService.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, 1899, 2);
        Leitor leitor = leitorService.cadastrar("Maria Souza", CPF, "maria@exemplo.com");

        assertEquals(2, livro.getQuantidadeDisponivel());
        assertTrue(livro.isDisponivel());

        Emprestimo emprestimo = emprestimoService.emprestar(CPF, ISBN);

        assertEquals(leitor, emprestimo.getLeitor());
        assertEquals(livro, emprestimo.getLivro());
        assertEquals(LocalDate.of(2026, 11, 2), emprestimo.getDataPrevista());
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertEquals(1, livroRepositorio.buscarPorIsbn(ISBN).orElseThrow().getQuantidadeDisponivel());
        assertTrue(livroRepositorio.buscarPorIsbn(ISBN).orElseThrow().isDisponivel());
        assertEquals(1, emprestimoRepositorio.listarAtivosPorLeitor(leitor).size());
    }
}
