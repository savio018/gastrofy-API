package com.gastrofy.gastrofyapi.integration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SegurancaTest extends IntegrationTestBase {

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios/me", "/insumos", "/receitas", "/produtos",
            "/embalagens", "/clientes", "/pedidos", "/alertas"})
    void rotaProtegidaSemToken_retorna401(String rota) throws Exception {
        mockMvc.perform(get(rota))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios/me", "/insumos", "/receitas", "/produtos",
            "/embalagens", "/clientes", "/pedidos", "/alertas"})
    void rotaProtegidaComTokenInvalido_retorna401(String rota) throws Exception {
        mockMvc.perform(get(rota).header("Authorization", "Bearer token.invalido.qualquer"))
                .andExpect(status().isUnauthorized());
    }
}
