package com.gastrofy.gastrofyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "insumo")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Long id;

    @Column(name = "nome_insumo")
    private String nome;

    @Column(name = "unidade_consumo")
    private String unidadeConsumo;

    @Column(name = "unidade_compra")
    private String unidadeCompra;

    @Column(name = "conteudo_por_unidade_compra")
    private BigDecimal conteudoUnidadeCompra;

    @Column(name = "preco_por_unidade_compra")
    private BigDecimal precoUnidadeCompra;

    @Column(name = "estoque_minimo")
    private BigDecimal estoqueMinimo;

    @Column(name = "estoque_critico")
    private BigDecimal estoqueCritico;

    @Column(name = "dias_aviso_validade")
    private Integer diasAvisoValidade;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteInsumo> lotes = new ArrayList<>();
}