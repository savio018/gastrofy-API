package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReporEstoqueRequestDTO {

    @NotEmpty(message = "É necessário informar pelo menos um lote")
    @Valid
    private List<LoteInsumoRequestDTO> lotes;
}