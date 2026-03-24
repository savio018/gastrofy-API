package com.gastrofy.gastrofyapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter

public class InsumoRequestDTO {

    private String nome;
    private Date dataValidade;
    private BigDecimal quantidadeCompra;
    private String UnidadeConsumo;
    private String UnidadeCompra;
    private BigDecimal ConteudoUnidadeCompra;
    private BigDecimal PrecoUnidadeCompra;






}
