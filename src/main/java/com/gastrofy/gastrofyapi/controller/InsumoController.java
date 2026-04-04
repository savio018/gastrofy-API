package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.InsumoResponseDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.InsumoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @PostMapping
    public ResponseEntity<InsumoResponseDTO> criar(@Valid @RequestBody InsumoRequestDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1); // temporário

        InsumoResponseDTO insumo = insumoService.criar(dto, usuario);

        return ResponseEntity.ok(insumo);
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponseDTO>> listar() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        return ResponseEntity.ok(insumoService.listar(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> buscar(@PathVariable Long id) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        return ResponseEntity.ok(insumoService.buscarPorId(id, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody InsumoRequestDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1); // temporário

        InsumoResponseDTO insumoAtualizado = insumoService.atualizar(id, dto, usuario);

        return ResponseEntity.ok(insumoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        insumoService.deletar(id, usuario);

        return ResponseEntity.noContent().build();
    }
}
