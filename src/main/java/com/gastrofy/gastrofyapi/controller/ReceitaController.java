package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.ReceitaRequestDTO;
import com.gastrofy.gastrofyapi.dto.ReceitaResponseDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.ReceitaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    private final ReceitaService receitaService;

    public ReceitaController(ReceitaService receitaService) {
        this.receitaService = receitaService;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ReceitaResponseDTO> criar(@Valid @RequestBody ReceitaRequestDTO dto) {
        return ResponseEntity.ok(receitaService.criar(dto, getUsuarioAutenticado()));
    }

    @GetMapping
    public ResponseEntity<List<ReceitaResponseDTO>> listar() {
        return ResponseEntity.ok(receitaService.listar(getUsuarioAutenticado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(receitaService.buscarPorId(id, getUsuarioAutenticado()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReceitaRequestDTO dto) {
        return ResponseEntity.ok(receitaService.atualizar(id, dto, getUsuarioAutenticado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        receitaService.deletar(id, getUsuarioAutenticado());
        return ResponseEntity.noContent().build();
    }
}