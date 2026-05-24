package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceitaInsumoRequestDTO {

    @NotNull(message = "O ID do insumo é obrigatório")
    private Long insumoId;

    @NotNull(message = "Quantidade utilizada é obrigatória")
    private BigDecimal quantidadeUtilizada;
}