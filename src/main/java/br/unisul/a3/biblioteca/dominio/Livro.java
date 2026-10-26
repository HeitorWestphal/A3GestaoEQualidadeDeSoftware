package br.unisul.a3.biblioteca.dominio;

import java.util.Objects;

/**
 * Livro do acervo. O ISBN (normalizado) identifica o livro.
 */
public class Livro {

    private final String isbn;
    private final String titulo;
    private final String autor;
    private final int ano;
    private final int quantidadeTotal;
    private int quantidadeDisponivel;

    public Livro(String isbn, String titulo, String autor, int ano, int quantidadeTotal) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAno() {
        return ano;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public boolean isDisponivel() {
        return quantidadeDisponivel > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livro outro)) {
            return false;
        }
        return isbn.equals(outro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return titulo + " - " + autor + " (" + ano + ") ISBN " + isbn
                + " [" + quantidadeDisponivel + "/" + quantidadeTotal + " disponíveis]";
    }
}
