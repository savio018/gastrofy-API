
    package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface PedidoRepository extends JpaRepository<Pedido, Long> {

        List<Pedido> findByUsuarioIdUsuario(Integer usuarioId);

        Optional<Pedido> findByIdAndUsuarioIdUsuario(Long id, Integer usuarioId);
    }


