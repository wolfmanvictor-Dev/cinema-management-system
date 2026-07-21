package com.cineverse.dto;

import java.util.List;

/** Dados recebidos ao finalizar uma compra de ingressos. */
public class CompraRequest {

    private Long sessaoId;
    private List<Long> assentoIds;
    private String metodoPagamento;
    private Comprador comprador;

    public Long getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Long sessaoId) {
        this.sessaoId = sessaoId;
    }

    public List<Long> getAssentoIds() {
        return assentoIds;
    }

    public void setAssentoIds(List<Long> assentoIds) {
        this.assentoIds = assentoIds;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    /** Dados de quem está comprando o ingresso. */
    public static class Comprador {
        private String nome;
        private String email;
        private String cpf;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }
    }
}
