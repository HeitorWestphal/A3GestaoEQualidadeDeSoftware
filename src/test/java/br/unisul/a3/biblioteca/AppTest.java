package br.unisul.a3.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AppTest {

    @Test
    void deveRetornarMensagemDeBoasVindas() {
        assertEquals("Bem-vindo ao Sistema de Biblioteca!", App.mensagemBoasVindas());
    }

    @Test
    void deveIniciarPeloMetodoMain() {
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        try (PrintStream saidaCapturada = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(saidaCapturada);
            App.main(new String[0]);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }

        assertTrue(saida.toString(StandardCharsets.UTF_8).contains("Bem-vindo ao Sistema de Biblioteca!"));
    }

    @Test
    void deveCadastrarEConsultarLivroPeloMenu() {
        String entrada = String.join("\n",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "978-85-359-1484-9",
                "1899",
                "2",
                "2",
                "2",
                "casmurro",
                "0",
                "");
        String resultado = executarComEntrada(entrada);
        assertTrue(resultado.contains("Livro cadastrado: Dom Casmurro"));
        assertTrue(resultado.contains("Dom Casmurro - Machado de Assis"));
        assertTrue(resultado.contains("Disponível"));
    }

    @Test
    void deveConsultarPorIsbnETituloEAutor() {
        String entrada = String.join("\n",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "978-85-359-1484-9",
                "1899",
                "1",
                "2",
                "1",
                "9788535914849",
                "2",
                "2",
                "casmurro",
                "2",
                "3",
                "machado",
                "2",
                "3",
                "Clarice",
                "2",
                "0",
                "0",
                "");

        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Dom Casmurro - Machado de Assis"));
        assertTrue(resultado.contains("Nenhum livro encontrado."));
        assertTrue(resultado.contains("Encerrando o sistema."));
    }

    @Test
    void deveInformarOpcoesInvalidasErrosDeCadastroEConsultaSemResultado() {
        String entrada = String.join("\n",
                "x",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "ISBN inválido",
                "1899",
                "1",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "9788535914849",
                "1899",
                "1",
                "1",
                "Outro título",
                "Outro autor",
                "978-85-359-1484-9",
                "2000",
                "1",
                "2",
                "4",
                "2",
                "1",
                "",
                "2",
                "2",
                "não existe",
                "0",
                "");

        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Opção inválida."));
        assertTrue(resultado.contains("ISBN inválido"));
        assertTrue(resultado.contains("Já existe um livro cadastrado"));
        assertTrue(resultado.contains("Opção de consulta inválida."));
        assertTrue(resultado.contains("Termo de busca é obrigatório"));
        assertTrue(resultado.contains("Nenhum livro encontrado."));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1\n",
        "1\nDom Casmurro\n",
        "1\nDom Casmurro\nMachado de Assis\n",
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\n",
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\n1899\n"
    })
    void deveInterromperCadastroQuandoEntradaAcaba(String entrada) {
        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Cadastro de livro"));
        assertFalse(resultado.contains("Livro cadastrado:"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\nabc\n1\n0\n",
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\n1899\nabc\n0\n"
    })
    void deveRecusarAnoOuQuantidadeNaoNumericos(String entrada) {
        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Informe um número inteiro válido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2\n", "2\n1\n"})
    void deveInterromperConsultaQuandoEntradaAcaba(String entrada) {
        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Bem-vindo ao Sistema de Biblioteca!"));
        assertFalse(resultado.contains("Nenhum livro encontrado."));
    }

    private static String executarComEntrada(String entrada) {
        InputStream entradaSimulada = new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;

        try (PrintStream saidaCapturada = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setOut(saidaCapturada);
            new App(entradaSimulada).executar();
        } finally {
            System.setOut(saidaOriginal);
        }

        return saida.toString(StandardCharsets.UTF_8);
    }
}
