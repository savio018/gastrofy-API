package com.gastrofy.gastrofyapi.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.boot.model.source.spi.FetchCharacteristics;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "receita_insumo")
public class ReceitaInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id", nullable = false)
    private Receita receita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(name = "quantidade_utilizada", nullable = false)
    private BigDecimal quantidadeUtilizada;

}
