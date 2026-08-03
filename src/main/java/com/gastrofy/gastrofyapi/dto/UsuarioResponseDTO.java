package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UsuarioResponseDTO {

    private final Integer id;
    private final String nome;
    private final String email;
    private final LocalDate dataCriacao;

    public UsuarioResponseDTO(Integer id, String nome, String email, LocalDate dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
    }
}