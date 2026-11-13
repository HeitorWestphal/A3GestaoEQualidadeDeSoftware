package br.unisul.a3.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void deveRetornarMensagemDeBoasVindas() {
        assertEquals("Bem-vindo ao Sistema de Biblioteca!", App.mensagemBoasVindas());
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
        ByteArrayInputStream entradaSimulada = new ByteArrayInputStream(
                entrada.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;

        try (PrintStream saidaCapturada = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setOut(saidaCapturada);
            new App(entradaSimulada).executar();
        } finally {
            System.setOut(saidaOriginal);
        }

        String resultado = saida.toString(StandardCharsets.UTF_8);
        assertTrue(resultado.contains("Livro cadastrado: Dom Casmurro"));
        assertTrue(resultado.contains("Dom Casmurro - Machado de Assis"));
        assertTrue(resultado.contains("Disponível"));
    }
}
