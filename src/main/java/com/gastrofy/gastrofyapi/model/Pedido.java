package com.gastrofy.gastrofyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "observacao")
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    @Column(name = "total_venda")
    private BigDecimal totalVenda;

    @Column(name = "total_custo")
    private BigDecimal totalCusto;

    @Column(name = "total_lucro")
    private BigDecimal totalLucro;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
    }
}