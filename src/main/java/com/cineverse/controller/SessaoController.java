package com.cineverse.controller;

import com.cineverse.dto.SessaoRequest;
import com.cineverse.dto.SessaoResponse;
import com.cineverse.service.SessaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @GetMapping
    public List<SessaoResponse> listar() {
        return sessaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public SessaoResponse buscar(@PathVariable Long id) {
        return sessaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoResponse criar(@RequestBody SessaoRequest request) {
        return sessaoService.criar(request);
    }

    @PutMapping("/{id}")
    public SessaoResponse atualizar(@PathVariable Long id, @RequestBody SessaoRequest request) {
        return sessaoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        sessaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
