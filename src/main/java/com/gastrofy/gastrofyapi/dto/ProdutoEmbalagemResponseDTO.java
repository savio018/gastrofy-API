package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoEmbalagemResponseDTO {

    private Long embalagemId;
    private String nomeEmbalagem;
    private Integer quantidadeUtilizada;
}