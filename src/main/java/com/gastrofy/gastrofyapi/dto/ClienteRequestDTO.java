package com.gastrofy.gastrofyapi.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequestDTO {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String nome;

    private String telefone;

    @Email(message = "Email inválido")
    private String email;

    private String endereco;

    private String observacao;

}
