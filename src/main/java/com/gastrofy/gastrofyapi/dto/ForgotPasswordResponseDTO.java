package com.gastrofy.gastrofyapi.dto;

public class ForgotPasswordResponseDTO {

    private final String token;

    public ForgotPasswordResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

