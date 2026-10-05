package br.unisul.a3.biblioteca;

/**
 * Ponto de entrada do Sistema de Biblioteca.
 */
public final class App {

    static final String NOME_SISTEMA = "Sistema de Biblioteca";

    private App() {
    }

    public static void main(String[] args) {
        System.out.println(mensagemBoasVindas());
    }

    static String mensagemBoasVindas() {
        return "Bem-vindo ao " + NOME_SISTEMA + "!";
    }
}
