package com.cineverse.controller;

import com.cineverse.model.Sala;
import com.cineverse.service.SalaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public List<Sala> listar() {
        return salaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Sala buscar(@PathVariable Long id) {
        return salaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sala criar(@RequestBody Sala sala) {
        return salaService.criar(sala);
    }

    @PutMapping("/{id}")
    public Sala atualizar(@PathVariable Long id, @RequestBody Sala sala) {
        return salaService.atualizar(id, sala);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        salaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
