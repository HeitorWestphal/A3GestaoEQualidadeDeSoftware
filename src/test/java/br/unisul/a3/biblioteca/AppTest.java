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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @ParameterizedTest
    @ValueSource(strings = {
        "4\nMaria Souza\n529.982.247-24\nmaria@exemplo.com\n0\n|Erro: CPF inválido",
        "4\nMaria Souza\n529.982.247-25\nmaria.exemplo.com\n0\n|Erro: E-mail inválido",
        "4\n\n529.982.247-25\nmaria@exemplo.com\n0\n|Erro: Nome é obrigatório"
    })
    void deveMostrarErroENaoSalvarLeitorInvalido(String caso) {
        String[] partes = caso.split("\\|");
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        App app = new App(entradaSimulada(partes[0]),
                new LivroRepositorio(), leitorRepositorio, new EmprestimoRepositorio());

        String resultado = executar(app);

        assertTrue(resultado.contains(partes[1]));
        assertFalse(resultado.contains("Leitor cadastrado:"));
        assertTrue(leitorRepositorio.listarTodos().isEmpty());
    }

    @Test
    void deveMostrarErroAoCadastrarLeitorComCpfDuplicadoPeloMenu() {
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada("4\nOutro Nome\n52998224725\noutro@exemplo.com\n0\n"),
                new LivroRepositorio(), leitorRepositorio, new EmprestimoRepositorio());

        String resultado = executar(app);

        assertTrue(resultado.contains("Erro: Já existe um leitor cadastrado com este CPF"));
        assertEquals("Maria Souza", leitorRepositorio.buscarPorCpf("52998224725").orElseThrow().getNome());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "4\n",
        "4\nMaria Souza\n",
        "4\nMaria Souza\n529.982.247-25\n"
    })
    void deveInterromperCadastroDeLeitorQuandoEntradaAcaba(String entrada) {
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        App app = new App(entradaSimulada(entrada),
                new LivroRepositorio(), leitorRepositorio, new EmprestimoRepositorio());

        String resultado = executar(app);

        assertTrue(resultado.contains("Cadastro de leitor"));
        assertFalse(resultado.contains("Leitor cadastrado:"));
        assertFalse(resultado.contains("Erro:"));
        assertTrue(leitorRepositorio.listarTodos().isEmpty());
    }

    @Test
    void deveEmprestarLivroPeloMenuERecusarQuandoNaoHaExemplares() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        leitorRepositorio.salvar(new Leitor("11144477735", "João Lima", "joao@exemplo.com"));
        App app = new App(entradaSimulada("5\n529.982.247-25\n978-85-359-1484-9\n"
                + "5\n111.444.777-35\n9788535914849\n0\n"),
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        String dataPrevista = LocalDate.now().plusDays(Emprestimo.PRAZO_EM_DIAS)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertTrue(resultado.contains("5 - Emprestar livro"));
        assertTrue(resultado.contains("Empréstimo registrado: Dom Casmurro até " + dataPrevista));
        assertTrue(resultado.contains("Erro: Livro indisponível"));
        assertEquals(0, livro.getQuantidadeDisponivel());
        assertEquals(1, emprestimoRepositorio.listarTodos().size());
    }

    @Test
    void deveReduzirDisponibilidadeDeUmParaZeroAoEmprestarPeloMenu() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        Leitor maria = leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        assertEquals(1, livro.getQuantidadeDisponivel());
        App app = new App(entradaSimulada("5\n52998224725\n9788535914849\n0\n"),
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        executar(app);

        assertEquals(0, livro.getQuantidadeDisponivel());
        assertTrue(emprestimoRepositorio.buscarAtivoPorLeitorELivro(maria, livro).isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "5\n111.444.777-35\n9788535914849\n0\n|Erro: Leitor não cadastrado",
        "5\n529.982.247-25\n9788535911664\n0\n|Erro: Livro não cadastrado"
    })
    void deveMostrarErroAoEmprestarComLeitorOuLivroNaoCadastrado(String caso) {
        String[] partes = caso.split("\\|");
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada(partes[0]), livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains(partes[1]));
        assertFalse(resultado.contains("Empréstimo registrado:"));
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertTrue(emprestimoRepositorio.listarTodos().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"5\n", "5\n52998224725\n"})
    void deveInterromperEmprestimoQuandoEntradaAcaba(String entrada) {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada(entrada), livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains("Empréstimo de livro"));
        assertFalse(resultado.contains("Empréstimo registrado:"));
        assertFalse(resultado.contains("Erro:"));
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertTrue(emprestimoRepositorio.listarTodos().isEmpty());
    }

    @Test
    void deveDevolverLivroNoPrazoPeloMenuERecuperarDisponibilidadeSemMulta() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        Leitor maria = leitorRepositorio.salvar(
                new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        livro.retirarExemplar();
        Emprestimo emprestimo = emprestimoRepositorio.salvar(
                new Emprestimo(livro, maria, Clock.systemDefaultZone()));
        assertEquals(0, livro.getQuantidadeDisponivel());
        assertTrue(emprestimo.isAtivo());

        App app = new App(entradaSimulada("6\n529.982.247-25\n978-85-359-1484-9\n0\n"),
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains("6 - Devolver livro"));
        assertTrue(resultado.contains("Devolução registrada. Sem multa."));
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertTrue(livro.isDisponivel());
        assertFalse(emprestimo.isAtivo());
    }

    @Test
    void deveDevolverLivroComAtrasoEMostrarMultaPeloMenu() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        Leitor maria = leitorRepositorio.salvar(
                new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        livro.retirarExemplar();
        Clock relogioPassado = Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-9));
        Emprestimo emprestimo = emprestimoRepositorio.salvar(
                new Emprestimo(livro, maria, relogioPassado));
        assertEquals(0, livro.getQuantidadeDisponivel());
        assertTrue(emprestimo.isAtivo());

        App app = new App(entradaSimulada("6\n52998224725\n9788535914849\n0\n"),
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains("Devolução registrada. Multa: R$ 2,00"));
        assertEquals(1, livro.getQuantidadeDisponivel());
        assertTrue(livro.isDisponivel());
        assertFalse(emprestimo.isAtivo());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "6\n529.982.247-25\n978-85-359-1484-9\n0\n|Erro: Empréstimo inexistente",
        "6\n111.444.777-35\n9788535914849\n0\n|Erro: Leitor não cadastrado",
        "6\n529.982.247-25\n9788535911664\n0\n|Erro: Livro não cadastrado"
    })
    void deveMostrarErroAoDevolverSemEmprestimoAtivoENaoAlterarDisponibilidade(String caso) {
        String[] partes = caso.split("\\|");
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada(partes[0]), livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains(partes[1]));
        assertFalse(resultado.contains("Devolução registrada."));
        assertEquals(1, livro.getQuantidadeDisponivel());
    }

    @Test
    void deveMostrarErroAoDevolverEmprestimoJaEncerrado() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        Leitor maria = leitorRepositorio.salvar(
                new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        livro.retirarExemplar();
        Emprestimo emprestimo = emprestimoRepositorio.salvar(
                new Emprestimo(livro, maria, Clock.systemDefaultZone()));
        emprestimo.registrarDevolucao(Clock.systemDefaultZone());
        livro.devolverExemplar();

        App app = new App(entradaSimulada("6\n52998224725\n9788535914849\n0\n"),
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains("Erro: Empréstimo já encerrado"));
        assertFalse(resultado.contains("Devolução registrada."));
        assertEquals(1, livro.getQuantidadeDisponivel());
    }

    @ParameterizedTest
    @ValueSource(strings = {"6\n", "6\n52998224725\n"})
    void deveInterromperDevolucaoQuandoEntradaAcaba(String entrada) {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro livro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 1));
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada(entrada), livroRepositorio, leitorRepositorio, emprestimoRepositorio);

        String resultado = executar(app);

        assertTrue(resultado.contains("Devolução de livro"));
        assertFalse(resultado.contains("Devolução registrada."));
        assertFalse(resultado.contains("Erro:"));
        assertEquals(1, livro.getQuantidadeDisponivel());
    }

    @Test
    void deveMostrarHistoricoDoLeitorComEmprestimoAtivoEDevolvido() {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();
        Livro domCasmurro = livroRepositorio.salvar(
                new Livro("9788535914849", "Dom Casmurro", "Machado de Assis", 1899, 2));
        Livro memorias = livroRepositorio.salvar(
                new Livro("9788535911664", "Memórias Póstumas", "Machado de Assis", 1881, 2));
        Leitor maria = leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        Clock dezDiasAtras = Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-10));
        Emprestimo devolvido = emprestimoRepositorio.salvar(new Emprestimo(domCasmurro, maria, dezDiasAtras));
        devolvido.registrarDevolucao(Clock.systemDefaultZone());
        Emprestimo ativo = emprestimoRepositorio.salvar(
                new Emprestimo(memorias, maria, Clock.systemDefaultZone()));

        App app = new App(entradaSimulada("7\n529.982.247-25\n0\n"),
                livroRepositorio, leitorRepositorio, emprestimoRepositorio);
        String resultado = executar(app);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String linhaAtivo = "Livro: Memórias Póstumas | Data do empréstimo: "
                + ativo.getDataEmprestimo().format(formato) + " | Situação: Ativo";
        String linhaDevolvido = "Livro: Dom Casmurro | Data do empréstimo: "
                + devolvido.getDataEmprestimo().format(formato) + " | Situação: Devolvido";
        assertTrue(resultado.contains("7 - Histórico do leitor"));
        assertTrue(resultado.contains("Histórico do leitor"));
        assertTrue(resultado.contains(linhaAtivo));
        assertTrue(resultado.contains(linhaDevolvido));
        assertTrue(resultado.indexOf(linhaAtivo) < resultado.indexOf(linhaDevolvido));
        assertFalse(resultado.contains("Nenhum empréstimo encontrado."));
    }

    @Test
    void deveMostrarMensagemQuandoLeitorNaoTemEmprestimos() {
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada("7\n52998224725\n0\n"),
                new LivroRepositorio(), leitorRepositorio, new EmprestimoRepositorio());

        String resultado = executar(app);

        assertTrue(resultado.contains("Nenhum empréstimo encontrado."));
        assertFalse(resultado.contains("Livro:"));
    }

    @Test
    void deveMostrarErroNoHistoricoDeLeitorNaoCadastrado() {
        LeitorRepositorio leitorRepositorio = new LeitorRepositorio();
        leitorRepositorio.salvar(new Leitor("52998224725", "Maria Souza", "maria@exemplo.com"));
        App app = new App(entradaSimulada("7\n111.444.777-35\n0\n"),
                new LivroRepositorio(), leitorRepositorio, new EmprestimoRepositorio());

        String resultado = executar(app);

        assertTrue(resultado.contains("Erro: Leitor não cadastrado"));
        assertFalse(resultado.contains("Nenhum empréstimo encontrado."));
    }

    @Test
    void deveInterromperHistoricoQuandoEntradaAcaba() {
        String resultado = executarComEntrada("7\n");

        assertTrue(resultado.contains("Histórico do leitor"));
        assertFalse(resultado.contains("Nenhum empréstimo encontrado."));
        assertFalse(resultado.contains("Erro:"));
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
