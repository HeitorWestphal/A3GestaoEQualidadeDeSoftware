package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
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
    }

    @Test
    void livroDeveSerIgualASiMesmo() {
        Livro mesmaReferencia = livro;

        assertEquals(livro, mesmaReferencia);
    }

    @Test
    void livrosComIsbnDiferenteDevemSerDiferentes() {
        Livro outro = new Livro("8535914846", "Dom Casmurro", "Machado de Assis", 1899, 2);

        assertNotEquals(livro, outro);
    }

    @Test
    void livroNaoDeveSerIgualAObjetoDeOutroTipo() {
        Object isbnComoTexto = "9788535914849";

        boolean igual = livro.equals(isbnComoTexto);

        assertFalse(igual);
    }

    @Test
    void toStringDeveMostrarDisponibilidade() {
        assertEquals("Dom Casmurro - Machado de Assis (1899) ISBN 9788535914849 [2/2 disponíveis]",
                livro.toString());
    }

    @Test
    void deveDiminuirDisponibilidadeAoRetirarExemplar() {
        livro.retirarExemplar();

        assertEquals(1, livro.getQuantidadeDisponivel());
        assertTrue(livro.isDisponivel());
    }

    @Test
    void deveFicarIndisponivelQuandoRetiraOUltimoExemplar() {
        livro.retirarExemplar();
        livro.retirarExemplar();

        assertEquals(0, livro.getQuantidadeDisponivel());
        assertFalse(livro.isDisponivel());
    }

    @Test
    void deveRecusarRetiradaQuandoNaoHaExemplarDisponivel() {
        livro.retirarExemplar();
        livro.retirarExemplar();

        RegraNegocioException erro = assertThrows(RegraNegocioException.class, livro::retirarExemplar);

        assertEquals("Livro indisponível", erro.getMessage());
        assertEquals(0, livro.getQuantidadeDisponivel());
    }
}
