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
public class EmbalagemRequestDTO {

    @NotBlank(message = "Nome da embalagem é obrigatório")
    private String nome;

    @NotNull(message = "Quantidade de compra é obrigatória")
    @Positive(message = "Quantidade de compra deve ser maior que zero")
    private Integer quantidadeCompra;

    @NotBlank(message = "Unidade de compra é obrigatória")
    private String unidadeCompra;

    @NotNull(message = "Conteúdo por unidade de compra é obrigatório")
    @Positive(message = "Conteúdo por unidade de compra deve ser maior que zero")
    private Integer conteudoUnidadeCompra;

    @NotNull(message = "Preço por unidade de compra é obrigatório")
    @PositiveOrZero(message = "Preço por unidade de compra não pode ser negativo")
    private BigDecimal precoUnidadeCompra;

    private Integer estoqueMinimo;
    private Integer estoqueCritico;
}