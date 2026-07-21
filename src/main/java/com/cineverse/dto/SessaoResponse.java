package com.cineverse.dto;

import com.cineverse.model.Filme;
import com.cineverse.model.Sala;

/** Representação de uma sessão já enriquecida com os dados do filme e da sala. */
public class SessaoResponse {

    private Long id;
    private Filme filme;
    private Sala sala;
    private String data;
    private String horario;
    private double preco;
    private int assentosTotal;
    private int assentosDisponiveis;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getAssentosTotal() {
        return assentosTotal;
    }

    public void setAssentosTotal(int assentosTotal) {
        this.assentosTotal = assentosTotal;
    }

    public int getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(int assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }
}
