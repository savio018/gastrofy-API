package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InsumoUpdateRequestDTO {

    @NotBlank(message = "Nome do insumo é obrigatório")
    private String nome;

    @NotBlank(message = "Unidade de consumo é obrigatória")
    private String unidadeConsumo;

    @NotBlank(message = "Unidade de compra é obrigatória")
    private String unidadeCompra;

    @NotNull(message = "Conteúdo por unidade de compra é obrigatório")
    @Positive(message = "Conteúdo por unidade de compra deve ser maior que zero")
    private BigDecimal conteudoUnidadeCompra;

    @NotNull(message = "Preço por unidade de compra é obrigatório")
    @PositiveOrZero(message = "Preço por unidade de compra não pode ser negativo")
    private BigDecimal precoUnidadeCompra;

    private BigDecimal estoqueMinimo;
    private BigDecimal estoqueCritico;
    private Integer diasAvisoValidade;
}