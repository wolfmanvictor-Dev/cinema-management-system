package com.cineverse.repository;

import com.cineverse.model.Sala;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemorySalaRepository implements SalaRepository {

    private final Map<Long, Sala> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    @Override
    public List<Sala> listarTodas() {
        return dados.values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }

    @Override
    public Optional<Sala> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Sala salvar(Sala sala) {
        if (sala.getId() == null) {
            sala.setId(sequencia.incrementAndGet());
        }
        dados.put(sala.getId(), sala);
        return sala;
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
