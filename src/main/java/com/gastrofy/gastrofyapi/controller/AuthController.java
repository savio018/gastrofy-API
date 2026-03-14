package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.*;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import com.gastrofy.gastrofyapi.security.JwtService;
import com.gastrofy.gastrofyapi.service.EmailVerificationService;
import com.gastrofy.gastrofyapi.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas"));

        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), usuario.getSenha());

        if (!senhaCorreta) {
            throw new RegraNegocioException("Credenciais inválidas");
        }

        if (!usuario.isEmailVerificado()) {
            throw new RegraNegocioException("Email não verificado");
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        emailVerificationService.verificarEmail(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto
    ) {
        String token = passwordResetService.gerarTokenResetSenha(dto.getEmail());
        return ResponseEntity.ok(new ForgotPasswordResponseDTO(token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto
    ) {
        passwordResetService.redefinirSenha(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
}

