package com.cineverse.service;

import com.cineverse.dto.CompraRequest;
import com.cineverse.dto.CompraResponse;
import com.cineverse.exception.ApiException;
import com.cineverse.model.Assento;
import com.cineverse.model.Compra;
import com.cineverse.model.Filme;
import com.cineverse.model.Sala;
import com.cineverse.model.Sessao;
import com.cineverse.repository.AssentoRepository;
import com.cineverse.repository.CompraRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CompraService {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final List<String> METODOS_VALIDOS = List.of("cartao", "pix", "boleto");

    private final CompraRepository compraRepository;
    private final AssentoRepository assentoRepository;
    private final SessaoService sessaoService;
    private final FilmeService filmeService;
    private final SalaService salaService;

    // Trava simples para impedir que duas compras concorrentes reservem o mesmo assento.
    private final Object travaDeReserva = new Object();

    public CompraService(CompraRepository compraRepository, AssentoRepository assentoRepository,
                          SessaoService sessaoService, FilmeService filmeService, SalaService salaService) {
        this.compraRepository = compraRepository;
        this.assentoRepository = assentoRepository;
        this.sessaoService = sessaoService;
        this.filmeService = filmeService;
        this.salaService = salaService;
    }

    public List<CompraResponse> listarTodas() {
        return compraRepository.listarTodas().stream().map(this::montarResponse).toList();
    }

    public Compra finalizar(CompraRequest request) {
        validarDadosBasicos(request);

        synchronized (travaDeReserva) {
            List<Assento> assentosEscolhidos = carregarAssentosValidos(request);

            Compra compra = new Compra();
            compra.setCodigo(gerarCodigo());
            compra.setSessaoId(request.getSessaoId());
            compra.setAssentoIds(request.getAssentoIds());
            compra.setCompradorNome(request.getComprador().getNome().trim());
            compra.setCompradorEmail(request.getComprador().getEmail().trim());
            compra.setCompradorCpf(request.getComprador().getCpf().trim());
            compra.setMetodoPagamento(request.getMetodoPagamento());
            compra.setDataCriacao(LocalDateTime.now());

            double total = assentosEscolhidos.size() * obterPrecoDaSessao(request.getSessaoId());
            compra.setTotal(total);

            // Marca os assentos como ocupados
            assentosEscolhidos.forEach(assento -> {
                assento.setDisponivel(false);
                assentoRepository.salvar(assento);
            });

            return compraRepository.salvar(compra);
        }
    }

    // -------------------------------------------------------------------

    private void validarDadosBasicos(CompraRequest request) {
        if (request == null || request.getSessaoId() == null) {
            throw ApiException.badRequest("Sessão inválida.");
        }
        if (request.getAssentoIds() == null || request.getAssentoIds().isEmpty()) {
            throw ApiException.badRequest("Selecione ao menos um assento.");
        }
        if (request.getMetodoPagamento() == null || !METODOS_VALIDOS.contains(request.getMetodoPagamento())) {
            throw ApiException.badRequest("Selecione um método de pagamento válido.");
        }
        if (request.getComprador() == null
                || vazio(request.getComprador().getNome())
                || vazio(request.getComprador().getEmail())
                || vazio(request.getComprador().getCpf())) {
            throw ApiException.badRequest("Preencha todos os dados do comprador.");
        }
        if (!EMAIL_REGEX.matcher(request.getComprador().getEmail().trim()).matches()) {
            throw ApiException.badRequest("Informe um e-mail válido.");
        }
        String cpfNumeros = request.getComprador().getCpf().replaceAll("\\D", "");
        if (cpfNumeros.length() != 11) {
            throw ApiException.badRequest("Informe um CPF válido (11 dígitos).");
        }
    }

    private List<Assento> carregarAssentosValidos(CompraRequest request) {
        // Garante que a sessão existe
        sessaoService.buscarPorId(request.getSessaoId());

        List<Assento> assentosDaSessao = assentoRepository.listarPorSessao(request.getSessaoId());

        return request.getAssentoIds().stream().map(id -> {
            Assento assento = assentosDaSessao.stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> ApiException.badRequest("Assento inválido para esta sessão."));

            if (!assento.isDisponivel()) {
                throw ApiException.conflict(
                        "O assento " + assento.getCodigo() + " já foi reservado por outra pessoa. Escolha outro assento.");
            }
            return assento;
        }).collect(Collectors.toList());
    }

    private double obterPrecoDaSessao(Long sessaoId) {
        return sessaoService.buscarPorId(sessaoId).getPreco();
    }

    private String gerarCodigo() {
        return "CV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private CompraResponse montarResponse(Compra compra) {
        Sessao sessao = sessaoService.buscarSessaoBruta(compra.getSessaoId());
        Filme filme = filmeService.buscarPorId(sessao.getFilmeId());
        Sala sala = salaService.buscarPorId(sessao.getSalaId());

        String assentosTexto = assentoRepository.listarPorSessao(compra.getSessaoId()).stream()
                .filter(a -> compra.getAssentoIds().contains(a.getId()))
                .map(Assento::getCodigo)
                .collect(Collectors.joining(", "));

        CompraResponse response = new CompraResponse();
        response.setId(compra.getId());
        response.setCodigo(compra.getCodigo());
        response.setCompradorNome(compra.getCompradorNome());
        response.setCompradorEmail(compra.getCompradorEmail());
        response.setFilmeTitulo(filme.getTitulo());
        response.setSalaNome(sala.getNome());
        response.setAssentos(assentosTexto);
        response.setTotal(compra.getTotal());
        response.setMetodoPagamento(compra.getMetodoPagamento());
        response.setDataCriacao(compra.getDataCriacao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return response;
    }
}
