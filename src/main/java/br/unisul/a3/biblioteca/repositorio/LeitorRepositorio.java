package br.unisul.a3.biblioteca.repositorio;

import br.unisul.a3.biblioteca.dominio.Leitor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Armazena os leitores em memória, indexados pelo CPF.
 */
public class LeitorRepositorio {

    private final Map<String, Leitor> leitores = new LinkedHashMap<>();

    public Leitor salvar(Leitor leitor) {
        leitores.put(leitor.getCpf(), leitor);
        return leitor;
    }

    public Optional<Leitor> buscarPorCpf(String cpf) {
        return Optional.ofNullable(leitores.get(cpf));
    }

    public boolean existe(String cpf) {
        return leitores.containsKey(cpf);
    }

    public List<Leitor> listarTodos() {
        return new ArrayList<>(leitores.values());
    }
}
