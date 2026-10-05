package br.unisul.a3.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void deveRetornarMensagemDeBoasVindas() {
        assertEquals("Bem-vindo ao Sistema de Biblioteca!", App.mensagemBoasVindas());
    }

    @Test
    void mainDeveExecutarSemErros() {
        App.main(new String[0]);
    }
}
