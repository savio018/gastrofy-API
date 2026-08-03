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
@Table(name = "receita")
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_receita", nullable = false)
    private String nome;

    @Column(name = "rendimento", nullable = false)
    private BigDecimal rendimento;

    @Column(name = "unidade_rendimento", nullable = false)
    private String unidadeRendimento;

    @Column(name = "custo_total", nullable = false)
    private BigDecimal custoTotal;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaInsumo> insumos = new ArrayList<>();
}