package com.cineverse.model;

/** Representa um usuário administrador do painel. */
public class Admin {

    private Long id;
    private String usuario;
    private String senhaHash;
    private String nome;

    public Admin() {
    }

    public Admin(Long id, String usuario, String senhaHash, String nome) {
        this.id = id;
        this.usuario = usuario;
        this.senhaHash = senhaHash;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
