package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoteInsumoResponseDTO {

    private Long id;
    private BigDecimal quantidade;
    private LocalDate dataValidade;
    private LocalDate dataCompra;
}