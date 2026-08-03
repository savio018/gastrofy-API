package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.InsumoResponseDTO;
import com.gastrofy.gastrofyapi.dto.InsumoUpdateRequestDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.InsumoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<InsumoResponseDTO> criar(@Valid @RequestBody InsumoRequestDTO dto) {
        return ResponseEntity.ok(insumoService.criar(dto, getUsuarioAutenticado()));
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponseDTO>> listar() {
        return ResponseEntity.ok(insumoService.listar(getUsuarioAutenticado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(insumoService.buscarPorId(id, getUsuarioAutenticado()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody InsumoUpdateRequestDTO dto) {
        return ResponseEntity.ok(insumoService.atualizar(id, dto, getUsuarioAutenticado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        insumoService.deletar(id, getUsuarioAutenticado());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<InsumoResponseDTO> reporEstoque(
            @PathVariable Long id,
            @RequestParam BigDecimal quantidade) {
        return ResponseEntity.ok(insumoService.reporEstoque(id, quantidade, getUsuarioAutenticado()));
    }
}