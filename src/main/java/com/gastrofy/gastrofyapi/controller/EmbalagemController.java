package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.EmbalagemRequestDTO;
import com.gastrofy.gastrofyapi.dto.EmbalagemResponseDTO;
import com.gastrofy.gastrofyapi.dto.EmbalagemUpdateRequestDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.EmbalagemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/embalagens")
public class EmbalagemController {

    private final EmbalagemService embalagemService;

    public EmbalagemController(EmbalagemService embalagemService) {
        this.embalagemService = embalagemService;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<EmbalagemResponseDTO> criar(@Valid @RequestBody EmbalagemRequestDTO dto) {
        return ResponseEntity.ok(embalagemService.criar(dto, getUsuarioAutenticado()));
    }

    @GetMapping
    public ResponseEntity<List<EmbalagemResponseDTO>> listar() {
        return ResponseEntity.ok(embalagemService.listar(getUsuarioAutenticado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmbalagemResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(embalagemService.buscarPorId(id, getUsuarioAutenticado()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmbalagemResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmbalagemUpdateRequestDTO dto) {
        return ResponseEntity.ok(embalagemService.atualizar(id, dto, getUsuarioAutenticado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        embalagemService.deletar(id, getUsuarioAutenticado());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<EmbalagemResponseDTO> reporEstoque(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        return ResponseEntity.ok(embalagemService.reporEstoque(id, quantidade, getUsuarioAutenticado()));
    }
}
