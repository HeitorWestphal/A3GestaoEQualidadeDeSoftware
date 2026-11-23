package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CpfTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "52998224725",     // só dígitos
        "529.982.247-25",  // com pontuação
        "111.444.777-35",  // com pontuação
        "608.829.485-04"   // primeiro dígito verificador igual a 0
    })
    void deveAceitarCpfValido(String cpf) {
        assertTrue(Cpf.isValido(cpf));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "52998224724",     // segundo dígito verificador errado
        "52998224715",     // primeiro dígito verificador errado
        "11111111111",     // todos os dígitos iguais
        "5299822472",      // 10 dígitos
        "529982247251",    // 12 dígitos
        "529.982.247-2A"   // letra no lugar de dígito
    })
    void deveRecusarCpfInvalido(String cpf) {
        assertFalse(Cpf.isValido(cpf));
    }

    @Test
    void deveNormalizarRemovendoPontuacaoEEspacos() {
        assertEquals("52998224725", Cpf.normalizar(" 529.982.247-25 "));
    }

    @Test
    void deveNormalizarNuloComoTextoVazio() {
        assertEquals("", Cpf.normalizar(null));
    }
}
