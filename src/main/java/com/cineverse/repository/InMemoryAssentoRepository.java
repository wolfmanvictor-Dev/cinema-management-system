package com.cineverse.repository;

import com.cineverse.model.Assento;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAssentoRepository implements AssentoRepository {

    private final Map<Long, Assento> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    @Override
    public List<Assento> listarPorSessao(Long sessaoId) {
        return dados.values().stream()
                .filter(a -> a.getSessaoId().equals(sessaoId))
                .sorted((a, b) -> {
                    int cmpFileira = a.getFileira().compareTo(b.getFileira());
                    return cmpFileira != 0 ? cmpFileira : Integer.compare(a.getNumero(), b.getNumero());
                })
                .toList();
    }

    @Override
    public Optional<Assento> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Assento> salvarTodos(List<Assento> assentos) {
        assentos.forEach(this::salvar);
        return assentos;
    }

    @Override
    public Assento salvar(Assento assento) {
        if (assento.getId() == null) {
            assento.setId(sequencia.incrementAndGet());
        }
        dados.put(assento.getId(), assento);
        return assento;
    }

    @Override
    public void excluirPorSessao(Long sessaoId) {
        dados.values().removeIf(a -> a.getSessaoId().equals(sessaoId));
    }
}
