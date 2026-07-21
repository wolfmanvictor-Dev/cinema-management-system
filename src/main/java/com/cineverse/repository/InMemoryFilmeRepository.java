package com.cineverse.repository;

import com.cineverse.model.Filme;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryFilmeRepository implements FilmeRepository {

    private final Map<Long, Filme> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    @Override
    public List<Filme> listarTodos() {
        return dados.values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }

    @Override
    public Optional<Filme> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Filme salvar(Filme filme) {
        if (filme.getId() == null) {
            filme.setId(sequencia.incrementAndGet());
        }
        dados.put(filme.getId(), filme);
        return filme;
    }

    @Override
    public void excluir(Long id) {
        dados.remove(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return dados.containsKey(id);
    }
}
