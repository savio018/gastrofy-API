package com.gastrofy.gastrofyapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReceitaDuplicidadeTest extends IntegrationTestBase {

    @Test
    void nomeDuplicadoDa400_eForcarCriacaoCria() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "1000", LocalDate.now().plusDays(30));

        criarReceita(u.token(), "Bolo de Cenoura", itemReceita(insumo, "100"));

        String outraVersao = receitaJson("Bolo de Cenoura", itemReceita(insumo, "200"));

        postAuth("/receitas", u.token(), outraVersao)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Já existe uma receita chamada")))
                .andExpect(jsonPath("$.message", not(containsString("exatamente"))));

        postAuth("/receitas?forcarCriacao=true", u.token(), outraVersao)
                .andExpect(status().isOk());

        String json = corpo(getAuth("/receitas", u.token()).andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$")).hasSize(2);
    }

    @Test
    void mesmosInsumosCom395e395ponto00ContamComoIguais() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "1000", LocalDate.now().plusDays(30));

        criarReceita(u.token(), "Pudim", itemReceita(insumo, "395"));

        postAuth("/receitas", u.token(), receitaJson("Pudim", itemReceita(insumo, "395.00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        containsString("exatamente os mesmos ingredientes e quantidades")));
    }
}
