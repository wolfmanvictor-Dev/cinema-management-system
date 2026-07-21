package com.cineverse.service;

import com.cineverse.dto.SessaoRequest;
import com.cineverse.dto.SessaoResponse;
import com.cineverse.exception.ApiException;
import com.cineverse.model.*;
import com.cineverse.repository.AssentoRepository;
import com.cineverse.repository.CompraRepository;
import com.cineverse.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessaoService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final SessaoRepository sessaoRepository;
    private final AssentoRepository assentoRepository;
    private final CompraRepository compraRepository;
    private final FilmeService filmeService;
    private final SalaService salaService;

    public SessaoService(SessaoRepository sessaoRepository, AssentoRepository assentoRepository,
                          CompraRepository compraRepository, FilmeService filmeService, SalaService salaService) {
        this.sessaoRepository = sessaoRepository;
        this.assentoRepository = assentoRepository;
        this.compraRepository = compraRepository;
        this.filmeService = filmeService;
        this.salaService = salaService;
    }

    public List<SessaoResponse> listarTodas() {
        return sessaoRepository.listarTodas().stream().map(this::montarResponse).toList();
    }

    public SessaoResponse buscarPorId(Long id) {
        Sessao sessao = obterOuFalhar(id);
        return montarResponse(sessao);
    }

    /** Retorna a entidade Sessao "crua" (sem enriquecer com filme/sala), para uso interno de outros services. */
    public Sessao buscarSessaoBruta(Long id) {
        return obterOuFalhar(id);
    }

    public SessaoResponse criar(SessaoRequest request) {
        Filme filme = validarFilme(request);
        Sala sala = validarSala(request);
        LocalDate data = validarData(request.getData());
        LocalTime horario = validarHorario(request.getHorario());
        double preco = validarPreco(request.getPreco());

        validarConflitoDeHorario(sala.getId(), data, horario, null);

        Sessao sessao = new Sessao(null, filme.getId(), sala.getId(), data, horario, preco);
        sessao = sessaoRepository.salvar(sessao);

        gerarAssentos(sessao, sala);

        return montarResponse(sessao);
    }

    public SessaoResponse atualizar(Long id, SessaoRequest request) {
        Sessao existente = obterOuFalhar(id);

        Filme filme = validarFilme(request);
        Sala sala = validarSala(request);
        LocalDate data = validarData(request.getData());
        LocalTime horario = validarHorario(request.getHorario());
        double preco = validarPreco(request.getPreco());

        validarConflitoDeHorario(sala.getId(), data, horario, id);

        boolean salaMudou = !existente.getSalaId().equals(sala.getId());

        existente.setFilmeId(filme.getId());
        existente.setSalaId(sala.getId());
        existente.setData(data);
        existente.setHorario(horario);
        existente.setPreco(preco);
        sessaoRepository.salvar(existente);

        // Se a sala mudou, o layout de assentos precisa ser refeito
        if (salaMudou) {
            boolean temAssentoOcupado = assentoRepository.listarPorSessao(id).stream().anyMatch(a -> !a.isDisponivel());
            if (temAssentoOcupado) {
                throw ApiException.conflict("Não é possível trocar a sala: já existem assentos vendidos nesta sessão.");
            }
            assentoRepository.excluirPorSessao(id);
            gerarAssentos(existente, sala);
        }

        return montarResponse(existente);
    }

    public void excluir(Long id) {
        obterOuFalhar(id);

        boolean possuiCompra = compraRepository.listarTodas().stream().anyMatch(c -> c.getSessaoId().equals(id));
        if (possuiCompra) {
            throw ApiException.conflict("Não é possível excluir: esta sessão já possui ingressos vendidos.");
        }

        assentoRepository.excluirPorSessao(id);
        sessaoRepository.excluir(id);
    }

    // -------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------

    private void gerarAssentos(Sessao sessao, Sala sala) {
        List<Assento> assentos = new ArrayList<>();
        TipoAssento tipoAssento = sala.getTipo() == TipoSala.VIP ? TipoAssento.VIP : TipoAssento.NORMAL;

        for (int f = 0; f < sala.getFileiras(); f++) {
            String letraFileira = String.valueOf((char) ('A' + f));
            for (int n = 1; n <= sala.getAssentosPorFileira(); n++) {
                assentos.add(new Assento(null, sessao.getId(), letraFileira, n, tipoAssento, true));
            }
        }

        assentoRepository.salvarTodos(assentos);
    }

    private Sessao obterOuFalhar(Long id) {
        return sessaoRepository.buscarPorId(id)
                .orElseThrow(() -> ApiException.notFound("Sessão não encontrada."));
    }

    private Filme validarFilme(SessaoRequest request) {
        if (request == null || request.getFilmeId() == null) {
            throw ApiException.badRequest("Selecione o filme da sessão.");
        }
        return filmeService.buscarPorId(request.getFilmeId());
    }

    private Sala validarSala(SessaoRequest request) {
        if (request.getSalaId() == null) {
            throw ApiException.badRequest("Selecione a sala da sessão.");
        }
        return salaService.buscarPorId(request.getSalaId());
    }

    private LocalDate validarData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw ApiException.badRequest("Informe a data da sessão.");
        }
        try {
            return LocalDate.parse(data.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw ApiException.badRequest("Data inválida. Use o formato AAAA-MM-DD.");
        }
    }

    private LocalTime validarHorario(String horario) {
        if (horario == null || horario.trim().isEmpty()) {
            throw ApiException.badRequest("Informe o horário da sessão.");
        }
        try {
            return LocalTime.parse(horario.trim(), FORMATO_HORA);
        } catch (DateTimeException e) {
            throw ApiException.badRequest("Horário inválido. Use o formato HH:MM.");
        }
    }

    private double validarPreco(Double preco) {
        if (preco == null || preco <= 0) {
            throw ApiException.badRequest("Informe um preço de ingresso maior que zero.");
        }
        return preco;
    }

    private void validarConflitoDeHorario(Long salaId, LocalDate data, LocalTime horario, Long ignorarSessaoId) {
        boolean conflito = sessaoRepository.listarTodas().stream()
                .filter(s -> ignorarSessaoId == null || !s.getId().equals(ignorarSessaoId))
                .anyMatch(s -> s.getSalaId().equals(salaId) && s.getData().equals(data) && s.getHorario().equals(horario));

        if (conflito) {
            throw ApiException.conflict("Já existe uma sessão cadastrada nesta sala, nesta data e horário.");
        }
    }

    private SessaoResponse montarResponse(Sessao sessao) {
        Filme filme = filmeService.buscarPorId(sessao.getFilmeId());
        Sala sala = salaService.buscarPorId(sessao.getSalaId());
        List<Assento> assentos = assentoRepository.listarPorSessao(sessao.getId());

        SessaoResponse response = new SessaoResponse();
        response.setId(sessao.getId());
        response.setFilme(filme);
        response.setSala(sala);
        response.setData(sessao.getData().format(FORMATO_DATA));
        response.setHorario(sessao.getHorario().format(FORMATO_HORA));
        response.setPreco(sessao.getPreco());
        response.setAssentosTotal(assentos.size());
        response.setAssentosDisponiveis((int) assentos.stream().filter(Assento::isDisponivel).count());

        return response;
    }
}
