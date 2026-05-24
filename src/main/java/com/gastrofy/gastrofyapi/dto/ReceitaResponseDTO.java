package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ReceitaResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal rendimento;
    private String unidadeRendimento;
    private BigDecimal custoTotal;
    private BigDecimal custoPorUnidade;
    private List<ReceitaInsumoResponseDTO> insumos;
}