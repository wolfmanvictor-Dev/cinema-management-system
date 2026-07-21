package com.cineverse.controller;

import com.cineverse.dto.CompraRequest;
import com.cineverse.dto.CompraResponse;
import com.cineverse.model.Compra;
import com.cineverse.service.CompraService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<CompraResponse> listar() {
        return compraService.listarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Compra finalizar(@RequestBody CompraRequest request) {
        return compraService.finalizar(request);
    }
}
