package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String endereco;
    private String observacao;

}
