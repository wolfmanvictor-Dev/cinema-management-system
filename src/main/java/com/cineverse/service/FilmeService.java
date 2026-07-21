package com.cineverse.service;

import com.cineverse.exception.ApiException;
import com.cineverse.model.Filme;
import com.cineverse.repository.FilmeRepository;
import com.cineverse.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final SessaoRepository sessaoRepository;

    public FilmeService(FilmeRepository filmeRepository, SessaoRepository sessaoRepository) {
        this.filmeRepository = filmeRepository;
        this.sessaoRepository = sessaoRepository;
    }

    public List<Filme> listarTodos() {
        return filmeRepository.listarTodos();
    }

    public Filme buscarPorId(Long id) {
        return filmeRepository.buscarPorId(id)
                .orElseThrow(() -> ApiException.notFound("Filme não encontrado."));
    }

    public Filme criar(Filme filme) {
        validar(filme);
        filme.setId(null);
        return filmeRepository.salvar(filme);
    }

    public Filme atualizar(Long id, Filme dados) {
        Filme existente = buscarPorId(id);
        validar(dados);

        existente.setTitulo(dados.getTitulo().trim());
        existente.setGenero(dados.getGenero());
        existente.setDuracao(dados.getDuracao());
        existente.setClassificacao(dados.getClassificacao());
        existente.setSinopse(dados.getSinopse());
        existente.setPosterUrl(dados.getPosterUrl());

        return filmeRepository.salvar(existente);
    }

    public void excluir(Long id) {
        buscarPorId(id);

        boolean possuiSessoes = sessaoRepository.listarTodas().stream()
                .anyMatch(s -> s.getFilmeId().equals(id));
        if (possuiSessoes) {
            throw ApiException.conflict("Não é possível excluir: este filme possui sessões cadastradas.");
        }

        filmeRepository.excluir(id);
    }

    private void validar(Filme filme) {
        if (filme == null || vazio(filme.getTitulo())) {
            throw ApiException.badRequest("Informe o título do filme.");
        }
        if (filme.getDuracao() != null && filme.getDuracao() <= 0) {
            throw ApiException.badRequest("A duração do filme deve ser maior que zero.");
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
