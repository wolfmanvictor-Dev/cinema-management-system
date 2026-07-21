package com.cineverse.service;

import com.cineverse.exception.ApiException;
import com.cineverse.model.Sala;
import com.cineverse.repository.SalaRepository;
import com.cineverse.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SessaoRepository sessaoRepository;

    public SalaService(SalaRepository salaRepository, SessaoRepository sessaoRepository) {
        this.salaRepository = salaRepository;
        this.sessaoRepository = sessaoRepository;
    }

    public List<Sala> listarTodas() {
        return salaRepository.listarTodas();
    }

    public Sala buscarPorId(Long id) {
        return salaRepository.buscarPorId(id)
                .orElseThrow(() -> ApiException.notFound("Sala não encontrada."));
    }

    public Sala criar(Sala sala) {
        validar(sala);
        sala.setId(null);
        return salaRepository.salvar(sala);
    }

    public Sala atualizar(Long id, Sala dados) {
        Sala existente = buscarPorId(id);
        validar(dados);

        existente.setNome(dados.getNome().trim());
        existente.setTipo(dados.getTipo());
        existente.setFileiras(dados.getFileiras());
        existente.setAssentosPorFileira(dados.getAssentosPorFileira());

        return salaRepository.salvar(existente);
    }

    public void excluir(Long id) {
        buscarPorId(id);

        boolean possuiSessoes = sessaoRepository.listarTodas().stream()
                .anyMatch(s -> s.getSalaId().equals(id));
        if (possuiSessoes) {
            throw ApiException.conflict("Não é possível excluir: esta sala possui sessões cadastradas.");
        }

        salaRepository.excluir(id);
    }

    private void validar(Sala sala) {
        if (sala == null || vazio(sala.getNome())) {
            throw ApiException.badRequest("Informe o nome da sala.");
        }
        if (sala.getTipo() == null) {
            throw ApiException.badRequest("Selecione o tipo da sala.");
        }
        if (sala.getFileiras() <= 0 || sala.getAssentosPorFileira() <= 0) {
            throw ApiException.badRequest("Informe a quantidade de fileiras e assentos por fileira (maior que zero).");
        }
        if (sala.getFileiras() > 26) {
            throw ApiException.badRequest("O número máximo de fileiras é 26 (A a Z).");
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
