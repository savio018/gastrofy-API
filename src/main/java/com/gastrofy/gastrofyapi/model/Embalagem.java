package com.gastrofy.gastrofyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "embalagem")
public class Embalagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_embalagem")
    private Long id;

    @Column(name = "nome_embalagem")
    private String nome;

    @Column(name = "estoque_total")
    private Integer estoqueTotal;

    @Column(name = "unidade_compra")
    private String unidadeCompra;

    @Column(name = "conteudo_por_unidade_compra")
    private Integer conteudoUnidadeCompra;

    @Column(name = "preco_por_unidade_compra")
    private BigDecimal precoUnidadeCompra;

    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo;

    @Column(name = "estoque_critico")
    private Integer estoqueCritico;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
