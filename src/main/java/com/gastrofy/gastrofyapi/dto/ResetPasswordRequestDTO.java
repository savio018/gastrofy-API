package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Token obrigatório")
    private String token;

    @NotBlank(message = "Nova senha obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String novaSenha;
}