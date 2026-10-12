package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private BigDecimal quantidadeReceita;

    @NotNull(message = "Preço de venda é obrigatório")
    private BigDecimal precoVenda;

    @Valid
    private List<ProdutoEmbalagemRequestDTO> embalagens = new ArrayList<>();
}
