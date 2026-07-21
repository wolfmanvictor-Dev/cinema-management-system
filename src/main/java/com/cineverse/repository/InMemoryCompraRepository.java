package com.cineverse.repository;

import com.cineverse.model.Compra;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCompraRepository implements CompraRepository {

    private final Map<Long, Compra> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    @Override
    public List<Compra> listarTodas() {
        return dados.values().stream()
                .sorted((a, b) -> b.getDataCriacao().compareTo(a.getDataCriacao()))
                .toList();
    }

    @Override
    public Compra salvar(Compra compra) {
        if (compra.getId() == null) {
            compra.setId(sequencia.incrementAndGet());
        }
        dados.put(compra.getId(), compra);
        return compra;
    }
}
