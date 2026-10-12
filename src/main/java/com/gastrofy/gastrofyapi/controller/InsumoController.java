package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.InsumoResponseDTO;
import com.gastrofy.gastrofyapi.dto.InsumoUpdateRequestDTO;
import com.gastrofy.gastrofyapi.dto.ReporEstoqueRequestDTO;
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

    @DeleteMapping("/{id}/lotes/{loteId}")
    public ResponseEntity<InsumoResponseDTO> excluirLote(
            @PathVariable Long id,
            @PathVariable Long loteId) {
        return ResponseEntity.ok(insumoService.excluirLote(id, loteId, getUsuarioAutenticado()));
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<InsumoResponseDTO> reporEstoque(
            @PathVariable Long id,
            @Valid @RequestBody ReporEstoqueRequestDTO dto) {
        return ResponseEntity.ok(insumoService.reporEstoque(id, dto, getUsuarioAutenticado()));
    }
    @PostMapping("/{id}/corrigir-estoque")
    public ResponseEntity<InsumoResponseDTO> corrigirEstoque(
            @PathVariable Long id,
            @RequestParam BigDecimal quantidade) {
        return ResponseEntity.ok(insumoService.corrigirEstoque(id, quantidade, getUsuarioAutenticado()));
    }
    @PatchMapping("/{id}/lotes/{loteId}")
    public ResponseEntity<InsumoResponseDTO> editarLote(
            @PathVariable Long id,
            @PathVariable Long loteId,
            @RequestParam BigDecimal quantidade) {
        return ResponseEntity.ok(insumoService.editarLote(id, loteId, quantidade, getUsuarioAutenticado()));
    }
}