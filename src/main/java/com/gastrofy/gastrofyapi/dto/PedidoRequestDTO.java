package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PedidoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    private String observacao;

    private LocalDateTime dataEntrega;

    @NotNull(message = "Lista de itens é obrigatória")
    @Size(min = 1, message = "O pedido deve ter pelo menos um item")
    private List<PedidoItemRequestDTO> itens;
}