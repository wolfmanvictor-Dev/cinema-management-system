package com.cineverse.controller;

import com.cineverse.model.Filme;
import com.cineverse.service.FilmeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filmes")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    @GetMapping
    public List<Filme> listar() {
        return filmeService.listarTodos();
    }

    @GetMapping("/{id}")
    public Filme buscar(@PathVariable Long id) {
        return filmeService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Filme criar(@RequestBody Filme filme) {
        return filmeService.criar(filme);
    }

    @PutMapping("/{id}")
    public Filme atualizar(@PathVariable Long id, @RequestBody Filme filme) {
        return filmeService.atualizar(id, filme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        filmeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
