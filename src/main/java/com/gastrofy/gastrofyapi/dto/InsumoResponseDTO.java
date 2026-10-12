package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InsumoResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal estoqueTotal;
    private String unidadeConsumo;
    private String unidadeCompra;
    private BigDecimal conteudoUnidadeCompra;
    private BigDecimal precoUnidadeCompra;
    private BigDecimal estoqueMinimo;
    private BigDecimal estoqueCritico;
    private Integer diasAvisoValidade;
    private List<LoteInsumoResponseDTO> lotes = new ArrayList<>();
}