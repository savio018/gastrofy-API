package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InsumoUpdateRequestDTO {

    @NotBlank(message = "Nome do insumo é obrigatório")
    private String nome;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    @NotBlank(message = "Unidade de consumo é obrigatória")
    private String unidadeConsumo;

    @NotBlank(message = "Unidade de compra é obrigatória")
    private String unidadeCompra;

    @NotNull(message = "Conteúdo por unidade de compra é obrigatório")
    private BigDecimal conteudoUnidadeCompra;

    @NotNull(message = "Preço por unidade de compra é obrigatório")
    private BigDecimal precoUnidadeCompra;

    private BigDecimal estoqueMinimo;
    private BigDecimal estoqueCritico;
    private Integer diasAvisoValidade;
}
