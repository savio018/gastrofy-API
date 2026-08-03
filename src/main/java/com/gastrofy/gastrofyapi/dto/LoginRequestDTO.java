package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha obrigatória")
    private String senha;
}