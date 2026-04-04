package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InsumoResponseDTO {

    private Long id;
    private String nome;
    private LocalDate dataValidade;
    private BigDecimal estoqueTotal;
    private String unidadeConsumo;
    private String unidadeCompra;
    private BigDecimal conteudoUnidadeCompra;
    private BigDecimal precoUnidadeCompra;
}

