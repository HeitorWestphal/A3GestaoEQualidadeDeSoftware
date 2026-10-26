package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LivroServiceTest {

    private static final String ISBN = "9788535914849";

    private LivroRepositorio repositorio;
    private LivroService service;

    @BeforeEach
    void setUp() {
        repositorio = new LivroRepositorio();
        Clock relogio = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        service = new LivroService(repositorio, relogio);
    }

    @Test
    void deveCadastrarLivroComTodosOsExemplaresDisponiveis() {
        Livro livro = service.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, 1899, 3);

        assertEquals("Dom Casmurro", livro.getTitulo());
        assertEquals("Machado de Assis", livro.getAutor());
        assertEquals(1899, livro.getAno());
        assertEquals(3, livro.getQuantidadeTotal());
        assertEquals(3, livro.getQuantidadeDisponivel());
        assertTrue(repositorio.existe(ISBN));
    }

    @Test
    void deveSalvarIsbnNormalizadoETextosSemEspacosExtras() {
        Livro livro = service.cadastrar("  Dom Casmurro ", " Machado de Assis ", "978-85-359-1484-9", 1899, 1);

        assertEquals(ISBN, livro.getIsbn());
        assertEquals("Dom Casmurro", livro.getTitulo());
        assertEquals("Machado de Assis", livro.getAutor());
    }

    @Test
    void deveRecusarIsbnInvalido() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Dom Casmurro", "Machado de Assis", "9788535914848", 1899, 1));

        assertEquals("ISBN inválido", erro.getMessage());
        assertTrue(repositorio.listarTodos().isEmpty());
    }

    @Test
    void deveRecusarIsbnDuplicadoMesmoComFormatacaoDiferente() {
        service.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, 1899, 1);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Outro título", "Outro autor", "978-85-359-1484-9", 2000, 1));

        assertEquals("Já existe um livro cadastrado com este ISBN", erro.getMessage());
        assertEquals(1, repositorio.listarTodos().size());
        assertEquals("Dom Casmurro", repositorio.buscarPorIsbn(ISBN).orElseThrow().getTitulo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveExigirTitulo(String titulo) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar(titulo, "Machado de Assis", ISBN, 1899, 1));

        assertEquals("Título é obrigatório", erro.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveExigirAutor(String autor) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Dom Casmurro", autor, ISBN, 1899, 1));

        assertEquals("Autor é obrigatório", erro.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1449, 2027})
    void deveRecusarAnoForaDoIntervalo(int ano) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, ano, 1));

        assertEquals("Ano deve estar entre 1450 e 2026", erro.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1450, 2026})
    void deveAceitarAnoNosLimites(int ano) {
        Livro livro = service.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, ano, 1);

        assertEquals(ano, livro.getAno());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void deveExigirPeloMenosUmExemplar(int quantidade) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, 1899, quantidade));

        assertEquals("Quantidade de exemplares deve ser pelo menos 1", erro.getMessage());
    }

    @Test
    void construtorPadraoDeveUsarRelogioDoSistema() {
        LivroService servicePadrao = new LivroService(new LivroRepositorio());

        Livro livro = servicePadrao.cadastrar("Dom Casmurro", "Machado de Assis", ISBN, 1899, 1);

        assertEquals(ISBN, livro.getIsbn());
    }
}
