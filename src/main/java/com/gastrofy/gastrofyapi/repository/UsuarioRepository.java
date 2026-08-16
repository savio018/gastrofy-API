package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.emailVerificado = false AND u.dataCriacao < :limite")
    void deletarUsuariosNaoVerificadosAntesDe(@Param("limite") LocalDate limite);
}