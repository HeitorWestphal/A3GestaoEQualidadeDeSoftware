package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConsultaAcervoServiceTest {

    private static final String ISBN = "9788535914849";

    private Livro livro;
    private ConsultaAcervoService service;

    @BeforeEach
    void setUp() {
        LivroRepositorio repositorio = new LivroRepositorio();
        livro = new Livro(ISBN, "Dom Casmurro", "Machado de Assis", 1899, 2);
        repositorio.salvar(livro);
        service = new ConsultaAcervoService(repositorio);
    }

    @ParameterizedTest
    @ValueSource(strings = {"9788535914849", "978-85-359-1484-9"})
    void deveBuscarLivroPorIsbnComOuSemHifens(String isbn) {
        assertEquals(livro, service.buscarPorIsbn(isbn).orElseThrow());
    }
}
