package br.unisul.a3.biblioteca.excecao;

/**
 * Lançada quando uma operação viola uma regra de negócio da biblioteca.
 * A mensagem é exibida diretamente ao bibliotecário.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
