package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.PasswordResetToken;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.PasswordResetTokenRepository;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final long EXPIRATION_HOURS = 1;

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public String gerarTokenResetSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o email informado"));

        tokenRepository.deleteByUsuario(usuario);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpirationDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));

        tokenRepository.save(resetToken);

        return token;
    }

    public void redefinirSenha(String token, String novaSenha) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RegraNegocioException("Token de redefinição inválido"));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RegraNegocioException("Token de redefinição expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        tokenRepository.delete(resetToken);
    }
}

