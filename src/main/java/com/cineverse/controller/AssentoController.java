package com.cineverse.controller;

import com.cineverse.model.Assento;
import com.cineverse.service.AssentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assentos")
public class AssentoController {

    private final AssentoService assentoService;

    public AssentoController(AssentoService assentoService) {
        this.assentoService = assentoService;
    }

    @GetMapping("/sessao/{sessaoId}")
    public List<Assento> listarPorSessao(@PathVariable Long sessaoId) {
        return assentoService.listarPorSessao(sessaoId);
    }

    @PatchMapping("/{id}")
    public Assento atualizarDisponibilidade(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean disponivel = Boolean.TRUE.equals(body.get("disponivel"));
        return assentoService.atualizarDisponibilidade(id, disponivel);
    }
}
