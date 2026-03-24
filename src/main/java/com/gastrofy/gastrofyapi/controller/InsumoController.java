package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.InsumoService;
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
    public ResponseEntity<Insumo> criar(@RequestBody InsumoRequestDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1); // temporário

        Insumo insumo = insumoService.criar(dto, usuario);

        return ResponseEntity.ok(insumo);
    }

    @GetMapping
    public ResponseEntity<List<Insumo>> listar() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        return ResponseEntity.ok(insumoService.listar(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insumo> buscar(@PathVariable Long id) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        return ResponseEntity.ok(insumoService.buscarPorId(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        insumoService.deletar(id, usuario);

        return ResponseEntity.noContent().build();
    }
}
