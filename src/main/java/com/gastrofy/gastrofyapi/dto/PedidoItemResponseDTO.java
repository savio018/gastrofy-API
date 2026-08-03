package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PedidoItemResponseDTO {

    private Long produtoId;
    private String nomeProduto;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal custoUnitario;
    private BigDecimal subtotalVenda;
    private BigDecimal subtotalCusto;
}