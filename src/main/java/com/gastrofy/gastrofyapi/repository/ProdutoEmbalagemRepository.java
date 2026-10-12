package com.gastrofy.gastrofyapi.repository;

import com.gastrofy.gastrofyapi.model.ProdutoEmbalagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoEmbalagemRepository extends JpaRepository<ProdutoEmbalagem, Long> {

    List<ProdutoEmbalagem> findByProdutoId(Long produtoId);
}
