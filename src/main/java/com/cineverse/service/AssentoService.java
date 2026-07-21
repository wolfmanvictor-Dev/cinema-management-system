package com.cineverse.service;

import com.cineverse.exception.ApiException;
import com.cineverse.model.Assento;
import com.cineverse.repository.AssentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssentoService {

    private final AssentoRepository assentoRepository;

    public AssentoService(AssentoRepository assentoRepository) {
        this.assentoRepository = assentoRepository;
    }

    public List<Assento> listarPorSessao(Long sessaoId) {
        return assentoRepository.listarPorSessao(sessaoId);
    }

    public Assento buscarPorId(Long id) {
        return assentoRepository.buscarPorId(id)
                .orElseThrow(() -> ApiException.notFound("Assento não encontrado."));
    }

    /** Usado pelo admin para liberar ou bloquear manualmente um assento (ex: manutenção). */
    public Assento atualizarDisponibilidade(Long id, boolean disponivel) {
        Assento assento = buscarPorId(id);
        assento.setDisponivel(disponivel);
        return assentoRepository.salvar(assento);
    }
}
