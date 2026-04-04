package com.gastrofy.gastrofyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name =  "insumo")
public class Insumo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Long id;

    @Column(name = "nome_insumo")
    private String nome;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "estoque_total_consumo")
    private BigDecimal estoqueTotal;

    @Column(name = "unidade_consumo")
    private String unidadeConsumo;

    @Column(name = "unidade_compra" )
    private String unidadeCompra;

    @Column(name = "conteudo_por_unidade_compra")
    private BigDecimal conteudoUnidadeCompra;

    @Column(name = "preco_por_unidade_compra")
    private BigDecimal precoUnidadeCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;





}

