package com.gastrofy.gastrofyapi.dto;

public class UsuarioCadastroResponseDTO {

    private final UsuarioResponseDTO usuario;
    private final String tokenVerificacaoEmail;

    public UsuarioCadastroResponseDTO(UsuarioResponseDTO usuario, String tokenVerificacaoEmail) {
        this.usuario = usuario;
        this.tokenVerificacaoEmail = tokenVerificacaoEmail;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public String getTokenVerificacaoEmail() {
        return tokenVerificacaoEmail;
    }
}

