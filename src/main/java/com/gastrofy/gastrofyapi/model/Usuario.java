package com.gastrofy.gastrofyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nome;
    private String email;

    @JsonIgnore
    private String senha;

    private LocalDate dataCriacao;

    private boolean emailVerificado;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDate.now();
    }
}