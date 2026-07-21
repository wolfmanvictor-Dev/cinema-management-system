package com.cineverse.service;

import com.cineverse.dto.DashboardResponse;
import com.cineverse.model.Assento;
import com.cineverse.model.Compra;
import com.cineverse.model.Filme;
import com.cineverse.model.Sessao;
import com.cineverse.repository.AssentoRepository;
import com.cineverse.repository.CompraRepository;
import com.cineverse.repository.FilmeRepository;
import com.cineverse.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FilmeRepository filmeRepository;
    private final SessaoRepository sessaoRepository;
    private final AssentoRepository assentoRepository;
    private final CompraRepository compraRepository;

    public DashboardService(FilmeRepository filmeRepository, SessaoRepository sessaoRepository,
                             AssentoRepository assentoRepository, CompraRepository compraRepository) {
        this.filmeRepository = filmeRepository;
        this.sessaoRepository = sessaoRepository;
        this.assentoRepository = assentoRepository;
        this.compraRepository = compraRepository;
    }

    public DashboardResponse gerar() {
        List<Compra> compras = compraRepository.listarTodas();
        List<Sessao> sessoes = sessaoRepository.listarTodas();

        int assentosOcupados = sessoes.stream()
                .mapToInt(s -> (int) assentoRepository.listarPorSessao(s.getId()).stream()
                        .filter(a -> !a.isDisponivel()).count())
                .sum();

        double receita = compras.stream().mapToDouble(Compra::getTotal).sum();

        DashboardResponse response = new DashboardResponse();
        response.setTotalFilmes(filmeRepository.listarTodos().size());
        response.setTotalSessoes(sessoes.size());
        response.setTotalCompras(compras.size());
        response.setReceita(receita);
        response.setAssentosOcupados(assentosOcupados);
        response.setUltimasCompras(montarUltimasCompras(compras));

        return response;
    }

    private List<DashboardResponse.CompraResumo> montarUltimasCompras(List<Compra> compras) {
        return compras.stream()
                .sorted(Comparator.comparing(Compra::getDataCriacao).reversed())
                .limit(8)
                .map(compra -> {
                    DashboardResponse.CompraResumo resumo = new DashboardResponse.CompraResumo();
                    resumo.setCodigo(compra.getCodigo());
                    resumo.setCompradorNome(compra.getCompradorNome());
                    resumo.setFilmeNome(nomeDoFilmeDaCompra(compra));
                    resumo.setAssentos(codigosDosAssentos(compra));
                    resumo.setTotal(compra.getTotal());
                    resumo.setDataCriacao(compra.getDataCriacao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return resumo;
                })
                .collect(Collectors.toList());
    }

    private String nomeDoFilmeDaCompra(Compra compra) {
        return sessaoRepository.buscarPorId(compra.getSessaoId())
                .flatMap(sessao -> filmeRepository.buscarPorId(sessao.getFilmeId()))
                .map(Filme::getTitulo)
                .orElse("—");
    }

    private String codigosDosAssentos(Compra compra) {
        List<Assento> assentos = assentoRepository.listarPorSessao(compra.getSessaoId());
        return assentos.stream()
                .filter(a -> compra.getAssentoIds().contains(a.getId()))
                .map(Assento::getCodigo)
                .collect(Collectors.joining(", "));
    }
}
