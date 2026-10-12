package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmbalagemRequestDTO {

    @NotBlank(message = "Nome da embalagem é obrigatório")
    private String nome;

    @NotNull(message = "Quantidade de compra é obrigatória")
    private Integer quantidadeCompra;

    @NotBlank(message = "Unidade de compra é obrigatória")
    private String unidadeCompra;

    @NotNull(message = "Conteúdo por unidade de compra é obrigatório")
    private Integer conteudoUnidadeCompra;

    @NotNull(message = "Preço por unidade de compra é obrigatório")
    private BigDecimal precoUnidadeCompra;

    private Integer estoqueMinimo;
    private Integer estoqueCritico;
}