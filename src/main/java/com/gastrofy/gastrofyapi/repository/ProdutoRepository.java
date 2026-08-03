package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByUsuarioIdUsuario(Integer usuarioId);

    Optional<Produto> findByIdAndUsuarioIdUsuario(Long id, Integer usuarioId);
}