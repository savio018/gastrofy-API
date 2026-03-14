package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.UsuarioCadastroResponseDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioRequestDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioResponseDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioCadastroResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dto) {

        UsuarioCadastroResponseDTO usuarioCriado = usuarioService.criar(dto);
        return ResponseEntity.ok(usuarioCriado);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {

        return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/teste")
    public String teste() {
        return "API funcionando";
    }
}
