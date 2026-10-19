package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoteInsumoRequestDTO {

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;
}
