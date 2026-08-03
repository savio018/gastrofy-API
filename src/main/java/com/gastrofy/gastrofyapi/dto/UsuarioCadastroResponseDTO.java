package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;

@Getter
public class UsuarioCadastroResponseDTO {

    private final UsuarioResponseDTO usuario;
    private final String tokenVerificacaoEmail;

    public UsuarioCadastroResponseDTO(UsuarioResponseDTO usuario, String tokenVerificacaoEmail) {
        this.usuario = usuario;
        this.tokenVerificacaoEmail = tokenVerificacaoEmail;
    }
}

