package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.Embalagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmbalagemRepository extends JpaRepository<Embalagem, Long> {

    List<Embalagem> findByUsuarioIdUsuario(Integer usuarioId);

    Optional<Embalagem> findByIdAndUsuarioIdUsuario(Long id, Integer usuarioId);
}
