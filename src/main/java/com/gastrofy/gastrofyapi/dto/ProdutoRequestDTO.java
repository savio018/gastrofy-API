package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProdutoRequestDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "ID da receita é obrigatório")
    private Long receitaId;

    @NotNull(message = "Quantidade da receita é obrigatória")
    @Positive(message = "Quantidade da receita deve ser maior que zero")
    private BigDecimal quantidadeReceita;

    @NotNull(message = "Preço de venda é obrigatório")
    @Positive(message = "Preço de venda deve ser maior que zero")
    private BigDecimal precoVenda;

    @Valid
    private List<ProdutoEmbalagemRequestDTO> embalagens = new ArrayList<>();
}
