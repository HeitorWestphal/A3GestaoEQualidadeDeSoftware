package br.unisul.a3.biblioteca.dominio;

/**
 * Validação do formato de e-mail (RF02): usuario@dominio.tld, sem espaços.
 */
public final class Email {

    private Email() {
    }

    public static boolean isValido(String email) {
        if (email == null) {
            return false;
        }
        String valor = email.trim();
        int arroba = valor.indexOf('@');
        if (arroba <= 0 || arroba != valor.lastIndexOf('@') || valor.contains(" ")) {
            return false;
        }
        return isDominioValido(valor.substring(arroba + 1));
    }

    private static boolean isDominioValido(String dominio) {
        int ultimoPonto = dominio.lastIndexOf('.');
        if (ultimoPonto <= 0 || dominio.startsWith(".") || dominio.contains("..")) {
            return false;
        }
        String extensao = dominio.substring(ultimoPonto + 1);
        return extensao.length() >= 2 && extensao.chars().allMatch(Character::isLetter);
    }
}
