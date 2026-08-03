package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.PedidoRequestDTO;
import com.gastrofy.gastrofyapi.dto.PedidoResponseDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.ok(pedidoService.criar(dto, getUsuarioAutenticado()));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(pedidoService.listar(getUsuarioAutenticado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id, getUsuarioAutenticado()));
    }

    @PostMapping("/{id}/produzir")
    public ResponseEntity<PedidoResponseDTO> produzir(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.produzir(id, getUsuarioAutenticado()));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id, getUsuarioAutenticado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pedidoService.deletar(id, getUsuarioAutenticado());
        return ResponseEntity.noContent().build();
    }
}
