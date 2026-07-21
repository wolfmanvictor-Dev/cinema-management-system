package com.cineverse.model;

/** Representa um assento específico de uma sessão. */
public class Assento {

    private Long id;
    private Long sessaoId;
    private String fileira;
    private int numero;
    private TipoAssento tipo;
    private boolean disponivel;

    public Assento() {
    }

    public Assento(Long id, Long sessaoId, String fileira, int numero, TipoAssento tipo, boolean disponivel) {
        this.id = id;
        this.sessaoId = sessaoId;
        this.fileira = fileira;
        this.numero = numero;
        this.tipo = tipo;
        this.disponivel = disponivel;
    }

    public String getCodigo() {
        return fileira + numero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Long sessaoId) {
        this.sessaoId = sessaoId;
    }

    public String getFileira() {
        return fileira;
    }

    public void setFileira(String fileira) {
        this.fileira = fileira;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public TipoAssento getTipo() {
        return tipo;
    }

    public void setTipo(TipoAssento tipo) {
        this.tipo = tipo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
