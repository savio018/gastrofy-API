package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertaResponseDTO {

    private String tipo;
    private String mensagem;
    private String insumo;

    public AlertaResponseDTO(String tipo, String mensagem, String insumo) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.insumo = insumo;
    }
}
