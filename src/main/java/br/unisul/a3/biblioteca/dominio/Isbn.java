package br.unisul.a3.biblioteca.dominio;

/**
 * Validação e normalização de ISBN-10 e ISBN-13 (RN01).
 */
public final class Isbn {

    private Isbn() {
    }

    /**
     * Remove hífens e espaços e converte o "x" final do ISBN-10 para maiúsculo.
     */
    public static String normalizar(String isbn) {
        if (isbn == null) {
            return "";
        }
        return isbn.replace("-", "").replace(" ", "").toUpperCase();
    }

    public static boolean isValido(String isbn) {
        String digitos = normalizar(isbn);
        if (digitos.length() == 10) {
            return isIsbn10Valido(digitos);
        }
        if (digitos.length() == 13) {
            return isIsbn13Valido(digitos);
        }
        return false;
    }

    private static boolean isIsbn10Valido(String isbn) {
        int soma = 0;
        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int valor;
            if (i == 9 && c == 'X') {
                valor = 10;
            } else if (Character.isDigit(c)) {
                valor = c - '0';
            } else {
                return false;
            }
            soma += valor * (10 - i);
        }
        return soma % 11 == 0;
    }

    private static boolean isIsbn13Valido(String isbn) {
        int soma = 0;
        for (int i = 0; i < 13; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            int peso = i % 2 == 0 ? 1 : 3;
            soma += (c - '0') * peso;
        }
        return soma % 10 == 0;
    }
}
