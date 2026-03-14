package com.gastrofy.gastrofyapi.dto;

import java.time.LocalDate;

public class UsuarioResponseDTO {

    private Integer id;
    private String nome;
    private String email;
    private LocalDate dataCriacao;

    public UsuarioResponseDTO(Integer id, String nome, String email, LocalDate dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;

    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }
}
