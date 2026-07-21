package com.cineverse.repository;

import com.cineverse.model.Compra;

import java.util.List;

public interface CompraRepository {
    List<Compra> listarTodas();
    Compra salvar(Compra compra);
}
