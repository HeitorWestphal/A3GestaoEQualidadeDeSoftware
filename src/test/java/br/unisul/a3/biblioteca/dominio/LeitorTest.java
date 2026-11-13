package br.unisul.a3.biblioteca.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class LeitorTest {

    private final Leitor leitor = new Leitor("52998224725", "Maria Souza", "maria@exemplo.com");

    @Test
    void deveGuardarOsDadosDoLeitor() {
        assertEquals("52998224725", leitor.getCpf());
        assertEquals("Maria Souza", leitor.getNome());
        assertEquals("maria@exemplo.com", leitor.getEmail());
    }

    @Test
    void leitoresComMesmoCpfDevemSerIguais() {
        Leitor mesmoCpf = new Leitor("52998224725", "Outro Nome", "outro@exemplo.com");

        assertEquals(leitor, mesmoCpf);
        assertEquals(leitor.hashCode(), mesmoCpf.hashCode());
    }

    @Test
    void leitorDeveSerIgualASiMesmo() {
        Leitor mesmaReferencia = leitor;

        assertEquals(leitor, mesmaReferencia);
    }

    @Test
    void leitoresComCpfDiferenteDevemSerDiferentes() {
        Leitor outro = new Leitor("11144477735", "Maria Souza", "maria@exemplo.com");

        assertNotEquals(leitor, outro);
    }

    @Test
    void leitorNaoDeveSerIgualAObjetoDeOutroTipo() {
        Object cpfComoTexto = "52998224725";

        boolean igual = leitor.equals(cpfComoTexto);

        assertFalse(igual);
    }

    @Test
    void toStringDeveMostrarNomeCpfEEmail() {
        assertEquals("Maria Souza - CPF 52998224725 - maria@exemplo.com", leitor.toString());
    }
}
