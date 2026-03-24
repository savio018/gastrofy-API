package com.gastrofy.gastrofyapi.repository;


import com.gastrofy.gastrofyapi.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    List<Insumo> findByUsuarioId(Integer usuarioId);


    Optional<Insumo> findByIdAndUsuarioId(Long id, Integer usuarioId);
}
