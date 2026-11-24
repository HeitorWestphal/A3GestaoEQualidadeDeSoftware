package br.unisul.a3.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unisul.a3.biblioteca.dominio.Emprestimo;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.dominio.Livro;
import br.unisul.a3.biblioteca.repositorio.EmprestimoRepositorio;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;
import br.unisul.a3.biblioteca.repositorio.LivroRepositorio;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AppTest {

    @Test
    void deveRetornarMensagemDeBoasVindas() {
        assertEquals("Bem-vindo ao Sistema de Biblioteca!", App.mensagemBoasVindas());
    }

    @Test
    void deveIniciarPeloMetodoMain() {
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        try (PrintStream saidaCapturada = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(saidaCapturada);
            App.main(new String[0]);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }

        assertTrue(saida.toString(StandardCharsets.UTF_8).contains("Bem-vindo ao Sistema de Biblioteca!"));
    }

    @Test
    void deveCadastrarEConsultarLivroPeloMenu() {
        String entrada = String.join("\n",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "978-85-359-1484-9",
                "1899",
                "2",
                "2",
                "2",
                "casmurro",
                "0",
                "");
        String resultado = executarComEntrada(entrada);
        assertTrue(resultado.contains("Livro cadastrado: Dom Casmurro"));
        assertTrue(resultado.contains("Dom Casmurro - Machado de Assis"));
        assertTrue(resultado.contains("Disponível"));
    }

    @Test
    void deveConsultarPorIsbnETituloEAutor() {
        String entrada = String.join("\n",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "978-85-359-1484-9",
                "1899",
                "1",
                "2",
                "1",
                "9788535914849",
                "2",
                "2",
                "casmurro",
                "2",
                "3",
                "machado",
                "2",
                "3",
                "Clarice",
                "2",
                "0",
                "0",
                "");

        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Dom Casmurro - Machado de Assis"));
        assertTrue(resultado.contains("Nenhum livro encontrado."));
        assertTrue(resultado.contains("Encerrando o sistema."));
    }

    @Test
    void deveInformarOpcoesInvalidasErrosDeCadastroEConsultaSemResultado() {
        String entrada = String.join("\n",
                "x",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "ISBN inválido",
                "1899",
                "1",
                "1",
                "Dom Casmurro",
                "Machado de Assis",
                "9788535914849",
                "1899",
                "1",
                "1",
                "Outro título",
                "Outro autor",
                "978-85-359-1484-9",
                "2000",
                "1",
                "2",
                "4",
                "2",
                "1",
                "",
                "2",
                "2",
                "não existe",
                "0",
                "");

        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Opção inválida."));
        assertTrue(resultado.contains("ISBN inválido"));
        assertTrue(resultado.contains("Já existe um livro cadastrado"));
        assertTrue(resultado.contains("Opção de consulta inválida."));
        assertTrue(resultado.contains("Termo de busca é obrigatório"));
        assertTrue(resultado.contains("Nenhum livro encontrado."));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1\n",
        "1\nDom Casmurro\n",
        "1\nDom Casmurro\nMachado de Assis\n",
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\n",
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\n1899\n"
    })
    void deveInterromperCadastroQuandoEntradaAcaba(String entrada) {
        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Cadastro de livro"));
        assertFalse(resultado.contains("Livro cadastrado:"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\nabc\n1\n0\n",
        "1\nDom Casmurro\nMachado de Assis\n9788535914849\n1899\nabc\n0\n"
    })
    void deveRecusarAnoOuQuantidadeNaoNumericos(String entrada) {
        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Informe um número inteiro válido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2\n", "2\n1\n"})
    void deveInterromperConsultaQuandoEntradaAcaba(String entrada) {
        String resultado = executarComEntrada(entrada);

        assertTrue(resultado.contains("Bem-vindo ao Sistema de Biblioteca!"));
        assertFalse(resultado.contains("Nenhum livro encontrado."));
    }

    @Test
    void deveMostrarRelatorioDeAtrasosPeloMenu() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro domCasmurro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2));
        Livro memorias = livroRepositorio.salvar(
                new Livro("9788535911664", "Memórias Póstumas", "Machado de Assis", 1881, 2));
        Leitor maria = leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        // Emprestado há 10 dias com prazo de 7 -> 3 dias de atraso
        Clock dezDiasAtras = Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-10));
        emprestimoRepositorio.salvar(new Emprestimo(domCasmurro, maria, dezDiasAtras));
        // Emprestado hoje -> ainda no prazo
        emprestimoRepositorio.salvar(new Emprestimo(memorias, maria, Clock.systemDefaultZone()));

        App app = new App(entradaSimulada("3\n0\n"), livroRepositorio, leitorRepositorio, emprestimoRepositorio);
        String resultado = executar(app);

        assertTrue(resultado.contains(
                "Livro: Dom Casmurro | Leitor: Maria Souza | Dias de atraso: 3 | Multa prevista: R$ 3,00"));
        assertFalse(resultado.contains("Livro: Memórias Póstumas"));
    }

    @Test
    void deveInformarQuandoNaoHaEmprestimosEmAtraso() {
        String resultado = executarComEntrada("3\n0\n");

        assertTrue(resultado.contains("Relatório de empréstimos em atraso"));
        assertTrue(resultado.contains("Nenhum empréstimo em atraso."));
    }

    @Test
    void deveCadastrarLeitorPeloMenuESalvarNoRepositorio() {
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        App app = new App(entradaSimulada("4\nMaria Souza\n529.982.247-25\nmaria@exemplo.com\n0\n"),
                new LivroRepositorio(), leitorRepositorio, new EmprestimoRepositorio());

        String resultado = executar(app);

        assertTrue(resultado.contains("4 - Cadastrar leitor"));
        assertTrue(resultado.contains("Leitor cadastrado: Maria Souza"));
        Leitor salvo = leitorRepositorio.buscarPorCpf("52998224725").orElseThrow();
        assertEquals("Maria Souza", salvo.getNome());
        assertEquals("maria@exemplo.com", salvo.getEmail());
    }

    private static String executarComEntrada(String entrada) {
        return executar(new App(entradaSimulada(entrada)));
    }

    private static InputStream entradaSimulada(String entrada) {
        return new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8));
    }

    private static String executar(App app) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;

        try (PrintStream saidaCapturada = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setOut(saidaCapturada);
            app.executar();
        } finally {
            System.setOut(saidaOriginal);
        }

        return saida.toString(StandardCharsets.UTF_8);
    }
}
