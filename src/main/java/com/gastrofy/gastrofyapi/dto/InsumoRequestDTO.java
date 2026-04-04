package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    private BigDecimal quantidadeCompra;

    @NotBlank(message = "Unidade de consumo é obrigatória")
    private String unidadeConsumo;

    @NotBlank(message = "Unidade de compra é obrigatória")
    private String unidadeCompra;

    @NotNull(message = "Conteúdo por unidade de compra é obrigatório")
    private BigDecimal conteudoUnidadeCompra;

    @NotNull(message = "Preço por unidade de compra é obrigatório")
    private BigDecimal precoUnidadeCompra;
}

