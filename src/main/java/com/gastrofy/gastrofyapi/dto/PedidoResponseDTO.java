package com.gastrofy.gastrofyapi.dto;

import com.gastrofy.gastrofyapi.model.StatusPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private String nomeCliente;
    private String telefone;
    private String observacao;
    private StatusPedido status;
    private BigDecimal totalVenda;
    private BigDecimal totalCusto;
    private BigDecimal totalLucro;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEntrega;
    private List<PedidoItemResponseDTO> itens;
}