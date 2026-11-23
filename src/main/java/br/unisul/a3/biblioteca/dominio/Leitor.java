package br.unisul.a3.biblioteca.dominio;

import java.util.Objects;

/**
 * Leitor da biblioteca. O CPF (normalizado) identifica o leitor.
 */
public class Leitor {

    private final String cpf;
    private final String nome;
    private final String email;

    public Leitor(String cpf, String nome, String email) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Leitor outro)) {
            return false;
        }
        return cpf.equals(outro.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return nome + " - CPF " + cpf + " - " + email;
    }
}
