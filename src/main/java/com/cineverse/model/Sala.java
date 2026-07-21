package com.cineverse.model;

/** Representa uma sala de cinema, com seu layout de assentos. */
public class Sala {

    private Long id;
    private String nome;
    private TipoSala tipo;
    private int fileiras;             // quantidade de fileiras (A, B, C...)
    private int assentosPorFileira;   // assentos em cada fileira

    public Sala() {
    }

    public Sala(Long id, String nome, TipoSala tipo, int fileiras, int assentosPorFileira) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.fileiras = fileiras;
        this.assentosPorFileira = assentosPorFileira;
    }

    public int getCapacidade() {
        return fileiras * assentosPorFileira;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public void setTipo(TipoSala tipo) {
        this.tipo = tipo;
    }

    public int getFileiras() {
        return fileiras;
    }

    public void setFileiras(int fileiras) {
        this.fileiras = fileiras;
    }

    public int getAssentosPorFileira() {
        return assentosPorFileira;
    }

    public void setAssentosPorFileira(int assentosPorFileira) {
        this.assentosPorFileira = assentosPorFileira;
    }
}
