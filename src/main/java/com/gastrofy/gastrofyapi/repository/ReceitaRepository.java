package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    List<Receita> findByUsuarioIdUsuario(Integer usuarioId);

    Optional<Receita> findByIdAndUsuarioIdUsuario(Long id, Integer usuarioId);
}