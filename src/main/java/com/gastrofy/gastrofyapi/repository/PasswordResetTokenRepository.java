package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.PasswordResetToken;
import com.gastrofy.gastrofyapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}

