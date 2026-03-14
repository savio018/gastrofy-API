package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.EmailVerificationToken;
import com.gastrofy.gastrofyapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}

