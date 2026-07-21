package com.cineverse.repository;

import com.cineverse.model.Sala;

import java.util.List;
import java.util.Optional;

public interface SalaRepository {
    List<Sala> listarTodas();
    Optional<Sala> buscarPorId(Long id);
    Sala salvar(Sala sala);
    void excluir(Long id);
    boolean existePorId(Long id);
}
