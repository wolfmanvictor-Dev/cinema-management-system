package com.cineverse.repository;

import com.cineverse.model.Sessao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemorySessaoRepository implements SessaoRepository {

    private final Map<Long, Sessao> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    @Override
    public List<Sessao> listarTodas() {
        return dados.values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }

    @Override
    public Optional<Sessao> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Sessao salvar(Sessao sessao) {
        if (sessao.getId() == null) {
            sessao.setId(sequencia.incrementAndGet());
        }
        dados.put(sessao.getId(), sessao);
        return sessao;
    }

    @Override
    public void excluir(Long id) {
        dados.remove(id);
    }
}
