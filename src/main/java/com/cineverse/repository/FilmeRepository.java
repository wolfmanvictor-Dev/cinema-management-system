package com.cineverse.repository;

import com.cineverse.model.Filme;

import java.util.List;
import java.util.Optional;

public interface FilmeRepository {
    List<Filme> listarTodos();
    Optional<Filme> buscarPorId(Long id);
    Filme salvar(Filme filme);
    void excluir(Long id);
    boolean existePorId(Long id);
}
