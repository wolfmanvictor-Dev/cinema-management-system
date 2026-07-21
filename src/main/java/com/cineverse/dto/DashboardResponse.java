package com.cineverse.dto;

import java.util.List;

public class DashboardResponse {

    private int totalFilmes;
    private int totalSessoes;
    private int totalCompras;
    private double receita;
    private int assentosOcupados;
    private List<CompraResumo> ultimasCompras;

    public int getTotalFilmes() {
        return totalFilmes;
    }

    public void setTotalFilmes(int totalFilmes) {
        this.totalFilmes = totalFilmes;
    }

    public int getTotalSessoes() {
        return totalSessoes;
    }

    public void setTotalSessoes(int totalSessoes) {
        this.totalSessoes = totalSessoes;
    }

    public int getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(int totalCompras) {
        this.totalCompras = totalCompras;
    }

    public double getReceita() {
        return receita;
    }

    public void setReceita(double receita) {
        this.receita = receita;
    }

    public int getAssentosOcupados() {
        return assentosOcupados;
    }

    public void setAssentosOcupados(int assentosOcupados) {
        this.assentosOcupados = assentosOcupados;
    }

    public List<CompraResumo> getUltimasCompras() {
        return ultimasCompras;
    }

    public void setUltimasCompras(List<CompraResumo> ultimasCompras) {
        this.ultimasCompras = ultimasCompras;
    }

    /** Versão resumida de uma compra, usada nas listagens do dashboard. */
    public static class CompraResumo {
        private String codigo;
        private String compradorNome;
        private String filmeNome;
        private String assentos;
        private double total;
        private String dataCriacao;

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCompradorNome() {
            return compradorNome;
        }

        public void setCompradorNome(String compradorNome) {
            this.compradorNome = compradorNome;
        }

        public String getFilmeNome() {
            return filmeNome;
        }

        public void setFilmeNome(String filmeNome) {
            this.filmeNome = filmeNome;
        }

        public String getAssentos() {
            return assentos;
        }

        public void setAssentos(String assentos) {
            this.assentos = assentos;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getDataCriacao() {
            return dataCriacao;
        }

        public void setDataCriacao(String dataCriacao) {
            this.dataCriacao = dataCriacao;
        }
    }
}
