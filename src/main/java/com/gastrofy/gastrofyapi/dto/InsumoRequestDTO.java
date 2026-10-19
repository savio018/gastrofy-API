package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InsumoRequestDTO {

    @NotBlank(message = "Nome do insumo é obrigatório")
    private String nome;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    @NotNull(message = "Quantidade de compra é obrigatória")
    @Positive(message = "Quantidade de compra deve ser maior que zero")
    private BigDecimal quantidadeCompra;

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

