package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceitaInsumoResponseDTO {

    private Long insumoId;
    private String nomeInsumo;
    private BigDecimal quantidadeUtilizada;
    private String unidadeConsumo;
    private BigDecimal custoInsumo;
}