package br.unisul.a3.biblioteca.servico;

import br.unisul.a3.biblioteca.dominio.Cpf;
import br.unisul.a3.biblioteca.dominio.Email;
import br.unisul.a3.biblioteca.dominio.Leitor;
import br.unisul.a3.biblioteca.excecao.RegraNegocioException;
import br.unisul.a3.biblioteca.repositorio.LeitorRepositorio;

/**
 * Cadastro de leitores (RF02).
 */
public class LeitorService {

    private final LeitorRepositorio repositorio;

    public LeitorService(LeitorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Leitor cadastrar(String nome, String cpf, String email) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome é obrigatório");
        }
        if (!Cpf.isValido(cpf)) {
            throw new RegraNegocioException("CPF inválido");
        }
        String cpfNormalizado = Cpf.normalizar(cpf);
        if (repositorio.existe(cpfNormalizado)) {
            throw new RegraNegocioException("Já existe um leitor cadastrado com este CPF");
        }
        if (!Email.isValido(email)) {
            throw new RegraNegocioException("E-mail inválido");
        }

        Leitor leitor = new Leitor(cpfNormalizado, nome.trim(), email.trim());
        return repositorio.salvar(leitor);
    }
}
