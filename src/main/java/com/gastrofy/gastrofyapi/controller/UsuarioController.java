package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.UsuarioCadastroResponseDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioRequestDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioResponseDTO;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioCadastroResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.criar(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obterPerfil() {
        return ResponseEntity.ok(usuarioService.buscarPorId(getUsuarioAutenticado().getIdUsuario()));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(getUsuarioAutenticado().getIdUsuario(), dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarConta() {
        usuarioService.deletar(getUsuarioAutenticado().getIdUsuario());
        return ResponseEntity.noContent().build();
    }

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }
}