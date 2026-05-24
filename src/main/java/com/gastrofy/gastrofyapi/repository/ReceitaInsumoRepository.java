package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.ReceitaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceitaInsumoRepository extends JpaRepository<ReceitaInsumo, Long> {

    List<ReceitaInsumo> findByReceitaId(Long receitaId);
}