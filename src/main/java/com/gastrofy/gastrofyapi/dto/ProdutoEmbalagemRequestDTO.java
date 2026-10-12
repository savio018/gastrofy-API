package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoEmbalagemRequestDTO {

    @NotNull(message = "O ID da embalagem é obrigatório")
    private Long embalagemId;

    @NotNull(message = "Quantidade utilizada é obrigatória")
    private Integer quantidadeUtilizada;
}
