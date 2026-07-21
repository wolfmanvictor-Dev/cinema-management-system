package com.cineverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API do CineVerse.
 *
 * Este projeto usa persistência EM MEMÓRIA (HashMap/ArrayList) de propósito,
 * para manter o foco em Spring MVC, camadas (Controller/Service/Repository)
 * e regras de negócio, sem exigir configuração de banco de dados.
 * Todos os dados são perdidos quando a aplicação é reiniciada.
 */
@SpringBootApplication
public class CineverseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineverseApplication.class, args);
    }
}
