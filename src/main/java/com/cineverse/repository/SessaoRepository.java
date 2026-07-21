package com.cineverse.repository;

import com.cineverse.model.Sessao;

import java.util.List;
import java.util.Optional;

public interface SessaoRepository {
    List<Sessao> listarTodas();
    Optional<Sessao> buscarPorId(Long id);
    Sessao salvar(Sessao sessao);
    void excluir(Long id);
}
