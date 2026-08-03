package com.gastrofy.gastrofyapi.controller;

import com.gastrofy.gastrofyapi.dto.*;
import com.gastrofy.gastrofyapi.service.AuthService;
import com.gastrofy.gastrofyapi.service.EmailVerificationService;
import com.gastrofy.gastrofyapi.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    /**
     * POST /auth/login - Realiza login e retorna token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * GET /auth/verify-email - Verifica email do usuário
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        emailVerificationService.verificarEmail(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /auth/forgot-password - Gera token para reset de senha
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto
    ) {
        String token = passwordResetService.gerarTokenResetSenha(dto.getEmail());
        return ResponseEntity.ok(new ForgotPasswordResponseDTO(token));
    }

    /**
     * POST /auth/reset-password - Reseta a senha do usuário
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto
    ) {
        passwordResetService.redefinirSenha(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
}

