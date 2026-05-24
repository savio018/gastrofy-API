package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ReceitaRequestDTO {

    @NotBlank(message = "Nome da receita é obrigatório")
    private String nome;

    @NotNull(message = "Rendimento é obrigatório")
    private BigDecimal rendimento;

    @NotBlank(message = "Unidade do rendimento é obrigatória")
    private String unidadeRendimento;

    @NotNull(message = "Lista de insumos é obrigatória")
    private List<ReceitaInsumoRequestDTO> insumos;
}
