package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.EmailVerificationToken;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.EmailVerificationTokenRepository;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final long EXPIRATION_HOURS = 24;

    private final EmailVerificationTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;

    public String criarTokenParaUsuario(Usuario usuario) {
        tokenRepository.deleteByUsuario(usuario);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken emailToken = new EmailVerificationToken();
        emailToken.setToken(token);
        emailToken.setUsuario(usuario);
        emailToken.setExpirationDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));

        tokenRepository.save(emailToken);

        return token;
    }

    public void verificarEmail(String token) {
        EmailVerificationToken emailToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RegraNegocioException("Token de verificação inválido"));

        if (emailToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(emailToken);
            throw new RegraNegocioException("Token de verificação expirado");
        }

        Usuario usuario = emailToken.getUsuario();
        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);

        tokenRepository.delete(emailToken);
    }
}

