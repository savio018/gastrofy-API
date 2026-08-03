package com.gastrofy.gastrofyapi.repository;


import com.gastrofy.gastrofyapi.model.Cliente;
import com.gastrofy.gastrofyapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByUsuarioIdUsuario(Integer usuarioId);

    Optional<Cliente> findByIdAndUsuarioIdUsuario(Long id, Integer usuarioId);
}
