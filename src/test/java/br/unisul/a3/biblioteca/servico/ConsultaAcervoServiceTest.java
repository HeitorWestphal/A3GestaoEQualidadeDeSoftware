package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ConsultaAcervoServiceTest {

    private static final String ISBN = "9788535914849";

    private LivroRepositorio repositorio;
    private Livro livro;
    private ConsultaAcervoService service;

    @BeforeEach
    void setUp() {
        repositorio = new LivroRepositorio();
        livro = new Livro(ISBN, "Dom Casmurro", "Machado de Assis", 1899, 2);
        repositorio.salvar(livro);
        service = new ConsultaAcervoService(repositorio);
    }

    @ParameterizedTest
    @ValueSource(strings = {"9788535914849", "978-85-359-1484-9"})
    void deveBuscarLivroPorIsbnComOuSemHifens(String isbn) {
        assertEquals(livro, service.buscarPorIsbn(isbn).orElseThrow());
    }

    @Test
    void deveBuscarTituloPorTrechoIgnorandoMaiusculasEMinusculas() {
        assertEquals(List.of(livro), service.buscarPorTitulo("casmurro"));
    }

    @Test
    void deveBuscarAutorPorTrechoIgnorandoMaiusculasEMinusculas() {
        assertEquals(List.of(livro), service.buscarPorAutor("machado"));
    }

    @Test
    void deveRetornarListaVaziaQuandoAutorNaoForEncontrado() {
        assertEquals(List.of(), service.buscarPorAutor("Clarice Lispector"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveRecusarTermoDeBuscaInvalidoPorIsbn(String termo) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.buscarPorIsbn(termo));

        assertEquals("Termo de busca é obrigatório", erro.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveRecusarTermoDeBuscaInvalidoPorTitulo(String termo) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.buscarPorTitulo(termo));

        assertEquals("Termo de busca é obrigatório", erro.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveRecusarTermoDeBuscaInvalidoPorAutor(String termo) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.buscarPorAutor(termo));

        assertEquals("Termo de busca é obrigatório", erro.getMessage());
    }

    @Test
    void deveEncontrarLivroSemExemplaresComoIndisponivel() {
        Livro livroSemExemplares = new Livro(
                "9780132350884", "Livro sem exemplares", "Autor desconhecido", 2008, 0);
        repositorio.salvar(livroSemExemplares);

        assertEquals(List.of(livroSemExemplares), service.buscarPorTitulo("sem exemplares"));
        assertFalse(livroSemExemplares.isDisponivel());
    }
}
