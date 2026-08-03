package com.gastrofy.gastrofyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long id;

    @Column(name = "nome_produto", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id", nullable = false)
    private Receita receita;

    @Column(name = "quantidade_receita", nullable = false)
    private BigDecimal quantidadeReceita;

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda;

    @Column(name = "custo_total")
    private BigDecimal custoTotal;

    @Column(name = "lucro")
    private BigDecimal lucro;

    @Column(name = "margem_lucro")
    private BigDecimal margemLucro;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}