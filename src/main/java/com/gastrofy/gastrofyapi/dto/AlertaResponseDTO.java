package com.gastrofy.gastrofyapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlertaResponseDTO {

    private String tipo;
    private String mensagem;
    private String insumo;      // nome do item (mantido para o frontend atual)
    private Long itemId;        // id do insumo ou da embalagem
    private Long loteId;        // só preenchido em alertas de validade
    private String categoria;   // "INSUMO" ou "EMBALAGEM"
}