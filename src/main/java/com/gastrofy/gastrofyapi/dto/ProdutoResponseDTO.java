package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Long receitaId;
    private String nomeReceita;
    private BigDecimal quantidadeReceita;
    private BigDecimal precoVenda;
    private BigDecimal custoTotal;
    private BigDecimal lucro;
    private BigDecimal margemLucro;
}