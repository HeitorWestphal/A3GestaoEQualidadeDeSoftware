package br.unisul.a3.biblioteca.dominio;

/**
 * Validação e normalização de CPF (RF02).
 */
public final class Cpf {

    private Cpf() {
    }

    /**
     * Remove pontos, hífen e espaços, deixando só os 11 dígitos.
     */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replace(".", "").replace("-", "").replace(" ", "");
    }

    public static boolean isValido(String cpf) {
        String digitos = normalizar(cpf);
        if (!digitos.matches("\\d{11}")) {
            return false;
        }
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        return calcularDigito(digitos, 9) == digitos.charAt(9) - '0'
                && calcularDigito(digitos, 10) == digitos.charAt(10) - '0';
    }

    private static int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
