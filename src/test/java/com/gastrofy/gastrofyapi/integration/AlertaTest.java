package com.gastrofy.gastrofyapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlertaTest extends IntegrationTestBase {

    @Test
    void alertas_vencidoPorLote_eEstoqueBaixoSemLote() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        LocalDate hoje = LocalDate.now();

        // Lote vencido ontem (10) + lote válido (10) = 20; mínimo 50, crítico 5 -> ESTOQUE_BAIXO
        long insumo = criarInsumo(u.token(), "1", "10", hoje.minusDays(1), "50", "5");
        String insumoJson = reporLotes(u.token(), insumo, lote("10", hoje.plusDays(60)));
        long loteVencido = ((Number) JsonPath.read(insumoJson, "$.lotes[0].id")).longValue();

        String json = corpo(getAuth("/alertas", u.token()).andExpect(status().isOk()));

        List<Map<String, Object>> vencidos = JsonPath.read(json, "$[?(@.tipo == 'VENCIDO')]");
        assertThat(vencidos).hasSize(1);
        assertThat(((Number) vencidos.get(0).get("loteId")).longValue()).isEqualTo(loteVencido);
        assertThat(((Number) vencidos.get(0).get("itemId")).longValue()).isEqualTo(insumo);

        List<Map<String, Object>> baixos = JsonPath.read(json, "$[?(@.tipo == 'ESTOQUE_BAIXO')]");
        assertThat(baixos).hasSize(1);
        assertThat(baixos.get(0).get("loteId")).isNull();
        assertThat(((Number) baixos.get(0).get("itemId")).longValue()).isEqualTo(insumo);
    }
}
