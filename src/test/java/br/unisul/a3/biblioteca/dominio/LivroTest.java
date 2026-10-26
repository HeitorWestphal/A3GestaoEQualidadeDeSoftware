package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LivroTest {

    private final Livro livro = new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2);

    @Test
    void novoLivroDeveTerTodosOsExemplaresDisponiveis() {
        assertEquals(2, livro.getQuantidadeDisponivel());
        assertTrue(livro.isDisponivel());
    }

    @Test
    void livrosComMesmoIsbnDevemSerIguais() {
        Livro mesmoIsbn = new Livro("9788535914849", "Outro título", "Outro autor", 2000, 1);

        assertEquals(livro, mesmoIsbn);
        assertEquals(livro.hashCode(), mesmoIsbn.hashCode());
        assertEquals(livro, livro);
    }

    @Test
    void livrosComIsbnDiferenteDevemSerDiferentes() {
        Livro outro = new Livro("8535914846", "Dom Casmurro", "Machado de Assis", 1899, 2);

        assertNotEquals(livro, outro);
        assertNotEquals(livro, "9788535914849");
    }

    @Test
    void toStringDeveMostrarDisponibilidade() {
        assertEquals("Dom Casmurro - Machado de Assis (1899) ISBN 9788535914849 [2/2 disponíveis]",
                livro.toString());
    }
}
