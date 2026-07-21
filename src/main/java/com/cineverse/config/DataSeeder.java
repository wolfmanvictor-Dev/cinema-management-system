package com.cineverse.config;

import com.cineverse.dto.SessaoRequest;
import com.cineverse.model.Filme;
import com.cineverse.model.Sala;
import com.cineverse.model.TipoSala;
import com.cineverse.repository.FilmeRepository;
import com.cineverse.repository.SalaRepository;
import com.cineverse.service.SessaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Popula a aplicação com filmes, salas e sessões de exemplo assim que ela
 * inicia, só para que o site não fique vazio na primeira execução.
 * Como a persistência é em memória, isso roda de novo a cada reinício.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;
    private final SessaoService sessaoService;

    public DataSeeder(FilmeRepository filmeRepository, SalaRepository salaRepository, SessaoService sessaoService) {
        this.filmeRepository = filmeRepository;
        this.salaRepository = salaRepository;
        this.sessaoService = sessaoService;
    }

    @Override
    public void run(String... args) {
        Filme f1 = filmeRepository.salvar(new Filme(null, "Nebulosa Escarlate", "Ficção Científica", 128, "12",
                "Uma tripulante solitária precisa decifrar um sinal alienígena antes que a nave se perca no vazio.",
                "https://images.unsplash.com/photo-1534809027769-b00d750a6bac?w=500&q=80"));
        Filme f2 = filmeRepository.salvar(new Filme(null, "Sombras de Ontem", "Suspense", 104, "14",
                "Um detetive é forçado a reabrir um caso do passado quando as evidências começam a se repetir.",
                "https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?w=500&q=80"));
        Filme f3 = filmeRepository.salvar(new Filme(null, "Risadas em Alto Mar", "Comédia", 96, "Livre",
                "Uma família desastrada tenta salvar as férias depois de perder o barco de cruzeiro.",
                "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?w=500&q=80"));
        Filme f4 = filmeRepository.salvar(new Filme(null, "Coração de Ferro", "Ação", 132, "16",
                "Um ex-soldado retorna à ativa para desmontar uma rede criminosa antes que seja tarde demais.",
                "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500&q=80"));
        Filme f5 = filmeRepository.salvar(new Filme(null, "O Jardim das Memórias", "Drama", 118, "10",
                "Uma avó e sua neta reconstroem a história da família através de um jardim esquecido.",
                "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=500&q=80"));
        Filme f6 = filmeRepository.salvar(new Filme(null, "Lenda do Vale Sombrio", "Terror", 101, "18",
                "Um grupo de amigos descobre que a lenda local sobre o vale é assustadoramente real.",
                "https://images.unsplash.com/photo-1509248961158-e54f6934749c?w=500&q=80"));

        Sala s1 = salaRepository.salvar(new Sala(null, "Sala 1", TipoSala.PEQUENA, 4, 6));
        Sala s2 = salaRepository.salvar(new Sala(null, "Sala 2", TipoSala.MEDIA, 6, 8));
        Sala s3 = salaRepository.salvar(new Sala(null, "Sala 3", TipoSala.GRANDE, 8, 12));
        Sala s4 = salaRepository.salvar(new Sala(null, "Sala VIP", TipoSala.VIP, 4, 5));

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatoData = DateTimeFormatter.ISO_LOCAL_DATE;

        criarSessao(f1.getId(), s3.getId(), hoje, "14:00", 28.0);
        criarSessao(f1.getId(), s3.getId(), hoje, "19:30", 32.0);
        criarSessao(f2.getId(), s2.getId(), hoje, "17:15", 26.0);
        criarSessao(f3.getId(), s1.getId(), hoje, "16:00", 22.0);
        criarSessao(f4.getId(), s4.getId(), hoje, "21:00", 45.0);
        criarSessao(f5.getId(), s2.getId(), hoje.plusDays(1), "15:30", 24.0);
        criarSessao(f6.getId(), s1.getId(), hoje.plusDays(1), "22:00", 27.0);
        criarSessao(f4.getId(), s3.getId(), hoje.plusDays(1), "20:00", 32.0);
        criarSessao(f1.getId(), s4.getId(), hoje.plusDays(2), "18:45", 45.0);
        criarSessao(f3.getId(), s2.getId(), hoje.plusDays(2), "14:30", 22.0);
    }

    private void criarSessao(Long filmeId, Long salaId, LocalDate data, String horario, double preco) {
        SessaoRequest request = new SessaoRequest();
        request.setFilmeId(filmeId);
        request.setSalaId(salaId);
        request.setData(data.format(DateTimeFormatter.ISO_LOCAL_DATE));
        request.setHorario(horario);
        request.setPreco(preco);
        sessaoService.criar(request);
    }
}
