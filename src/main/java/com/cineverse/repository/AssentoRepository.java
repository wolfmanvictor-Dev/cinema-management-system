package com.cineverse.repository;

import com.cineverse.model.Assento;

import java.util.List;
import java.util.Optional;

public interface AssentoRepository {
    List<Assento> listarPorSessao(Long sessaoId);
    Optional<Assento> buscarPorId(Long id);
    List<Assento> salvarTodos(List<Assento> assentos);
    Assento salvar(Assento assento);
    void excluirPorSessao(Long sessaoId);
}
