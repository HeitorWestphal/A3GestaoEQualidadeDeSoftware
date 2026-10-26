package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IsbnTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "9788535914849",      // ISBN-13
        "978-85-359-1484-9",  // ISBN-13 com hífens
        "8535914846",         // ISBN-10
        "0-8044-2957-X",      // ISBN-10 com dígito X
        "0-8044-2957-x"       // ISBN-10 com x minúsculo
    })
    void deveAceitarIsbnValido(String isbn) {
        assertTrue(Isbn.isValido(isbn));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "9788535914848",   // dígito verificador errado (13)
        "8535914847",      // dígito verificador errado (10)
        "978853591484",    // tamanho inválido
        "97885359148A9",   // letra no ISBN-13
        "85359A4846",      // letra no meio do ISBN-10
        "X535914846"       // X fora da última posição
    })
    void deveRecusarIsbnInvalido(String isbn) {
        assertFalse(Isbn.isValido(isbn));
    }

    @Test
    void deveNormalizarRemovendoHifensEEspacos() {
        assertEquals("080442957X", Isbn.normalizar(" 0-8044-2957-x "));
    }
}
