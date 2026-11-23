package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "maria@exemplo.com",
        "joao.silva+a3@unisul.edu.br",
        " maria@exemplo.com "   // espaços nas pontas são ignorados
    })
    void deveAceitarEmailValido(String email) {
        assertTrue(Email.isValido(email));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "maria.exemplo.com",    // sem @
        "@exemplo.com",         // sem usuário
        "maria@",               // sem domínio
        "maria@@exemplo.com",   // dois @
        "ma ria@exemplo.com",   // espaço no meio
        "maria@exemplo",        // domínio sem ponto
        "maria@.com",           // domínio começando com ponto
        "maria@exemplo..com",   // pontos seguidos
        "maria@exemplo.c",      // extensão com 1 letra
        "maria@exemplo.c0m"     // extensão com número
    })
    void deveRecusarEmailInvalido(String email) {
        assertFalse(Email.isValido(email));
    }
}
