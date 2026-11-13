package br.unisul.a3.biblioteca.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LeitorServiceTest {

    private static final String CPF = "52998224725";

    private LeitorRepositorio repositorio;
    private LeitorService service;

    @BeforeEach
    void setUp() {
        repositorio = new LeitorRepositorio();
        service = new LeitorService(repositorio);
    }

    @Test
    void deveCadastrarLeitorEDeixarDisponivelParaBuscaPorCpf() {
        Leitor leitor = service.cadastrar("Maria Souza", CPF, "maria@exemplo.com");

        assertEquals("Maria Souza", leitor.getNome());
        assertEquals(CPF, leitor.getCpf());
        assertEquals("maria@exemplo.com", leitor.getEmail());
        assertEquals(leitor, repositorio.buscarPorCpf(CPF).orElseThrow());
    }

    @Test
    void deveSalvarCpfNormalizadoETextosSemEspacosExtras() {
        Leitor leitor = service.cadastrar("  Maria Souza ", "529.982.247-25", " maria@exemplo.com ");

        assertEquals(CPF, leitor.getCpf());
        assertEquals("Maria Souza", leitor.getNome());
        assertEquals("maria@exemplo.com", leitor.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveExigirNome(String nome) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar(nome, CPF, "maria@exemplo.com"));

        assertEquals("Nome é obrigatório", erro.getMessage());
        assertTrue(repositorio.listarTodos().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"52998224724", "11111111111", "123"})
    void deveRecusarCpfInvalido(String cpf) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Maria Souza", cpf, "maria@exemplo.com"));

        assertEquals("CPF inválido", erro.getMessage());
        assertTrue(repositorio.listarTodos().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"maria.exemplo.com", "maria@exemplo"})
    void deveRecusarEmailInvalido(String email) {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> service.cadastrar("Maria Souza", CPF, email));

        assertEquals("E-mail inválido", erro.getMessage());
        assertTrue(repositorio.listarTodos().isEmpty());
    }
}
