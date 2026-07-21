package com.cineverse.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa uma sessão de exibição: um filme, numa sala, em uma data/horário,
 * com um preço de ingresso específico. Os assentos de cada sessão são gerados
 * automaticamente a partir do layout da sala escolhida.
 */
public class Sessao {

    private Long id;
    private Long filmeId;
    private Long salaId;
    private LocalDate data;
    private LocalTime horario;
    private double preco;

    public Sessao() {
    }

    public Sessao(Long id, Long filmeId, Long salaId, LocalDate data, LocalTime horario, double preco) {
        this.id = id;
        this.filmeId = filmeId;
        this.salaId = salaId;
        this.data = data;
        this.horario = horario;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilmeId() {
        return filmeId;
    }

    public void setFilmeId(Long filmeId) {
        this.filmeId = filmeId;
    }

    public Long getSalaId() {
        return salaId;
    }

    public void setSalaId(Long salaId) {
        this.salaId = salaId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}
