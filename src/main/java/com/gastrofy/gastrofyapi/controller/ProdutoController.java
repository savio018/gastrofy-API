package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.ProdutoRequestDTO;
import com.gastrofy.gastrofyapi.dto.ProdutoResponseDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(produtoService.criar(dto, getUsuarioAutenticado()));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar() {
        return ResponseEntity.ok(produtoService.listar(getUsuarioAutenticado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id, getUsuarioAutenticado()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(produtoService.atualizar(id, dto, getUsuarioAutenticado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id, getUsuarioAutenticado());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/produzir")
    public ResponseEntity<ProdutoResponseDTO> produzir(
            @PathVariable Long id,
            @RequestParam BigDecimal quantidade) {
        return ResponseEntity.ok(produtoService.produzir(id, quantidade, getUsuarioAutenticado()));
    }
}