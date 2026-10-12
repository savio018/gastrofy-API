package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmbalagemResponseDTO {

    private Long id;
    private String nome;
    private Integer estoqueTotal;
    private String unidadeCompra;
    private Integer conteudoUnidadeCompra;
    private BigDecimal precoUnidadeCompra;
    private Integer estoqueMinimo;
    private Integer estoqueCritico;
}
