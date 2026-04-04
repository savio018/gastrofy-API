package com.gastrofy.gastrofyapi.repository;


import com.gastrofy.gastrofyapi.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    List<Insumo> findByUsuarioIdUsuario(Integer usuarioId);


    Optional<Insumo> findByIdAndUsuarioIdUsuario(Long id, Integer usuarioId);
}
