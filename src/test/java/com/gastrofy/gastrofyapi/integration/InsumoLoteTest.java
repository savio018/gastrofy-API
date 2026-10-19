package com.gastrofy.gastrofyapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InsumoLoteTest extends IntegrationTestBase {

    private static final LocalDate HOJE = LocalDate.now();

    @Test
    void cicloDeLotes_criarReporEditarExcluir() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        // Criar gera exatamente 1 lote (2 x 1000 = 2000)
        long insumoId = criarInsumo(u.token(), "2", "1000", HOJE.plusDays(30));
        String json = corpo(getAuth("/insumos/" + insumoId, u.token()).andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(1);
        assertThat(decimal(json, "$.lotes[0].quantidade")).isEqualByComparingTo("2000");
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("2000");

        // Repor com 2 lotes soma ao estoque
        json = reporLotes(u.token(), insumoId,
                lote("500", HOJE.plusDays(10)) + "," + lote("300", HOJE.plusDays(20)));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(3);
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("2800");

        // Lotes vêm ordenados por validade: [0]=500(+10), [1]=300(+20), [2]=2000(+30)
        long loteDe500 = ((Number) JsonPath.read(json, "$.lotes[0].id")).longValue();
        long loteDe300 = ((Number) JsonPath.read(json, "$.lotes[1].id")).longValue();

        // Editar lote
        json = corpo(patchAuth("/insumos/" + insumoId + "/lotes/" + loteDe500 + "?quantidade=400",
                u.token(), null).andExpect(status().isOk()));
        assertThat(decimal(json, "$.lotes[0].quantidade")).isEqualByComparingTo("400");
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("2700");

        // Excluir lote
        json = corpo(deleteAuth("/insumos/" + insumoId + "/lotes/" + loteDe300, u.token())
                .andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(2);
        assertThat((List<?>) JsonPath.read(json, "$.lotes[?(@.id == " + loteDe300 + ")]")).isEmpty();
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("2400");
    }

    @Test
    void corrigirEstoque_descontaDoLoteQueVencePrimeiro_eLoteZeradoSome() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        LocalDate vencimentoTardio = HOJE.plusDays(30);
        LocalDate vencimentoCedo = HOJE.plusDays(5);

        long insumoId = criarInsumo(u.token(), "1", "100", vencimentoTardio); // lote 100 (+30)
        reporLotes(u.token(), insumoId, lote("50", vencimentoCedo));        // lote 50 (+5)

        // Perde 30: sai do lote que vence primeiro (50 -> 20); o de +30 fica intacto
        String json = corpo(postAuth("/insumos/" + insumoId + "/corrigir-estoque?quantidade=30",
                u.token(), null).andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(2);
        assertThat((String) JsonPath.read(json, "$.lotes[0].dataValidade")).isEqualTo(vencimentoCedo.toString());
        assertThat(decimal(json, "$.lotes[0].quantidade")).isEqualByComparingTo("20");
        assertThat(decimal(json, "$.lotes[1].quantidade")).isEqualByComparingTo("100");

        // Perde mais 20: o lote de +5 zera e desaparece
        json = corpo(postAuth("/insumos/" + insumoId + "/corrigir-estoque?quantidade=20",
                u.token(), null).andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(1);
        assertThat((String) JsonPath.read(json, "$.lotes[0].dataValidade")).isEqualTo(vencimentoTardio.toString());
        assertThat(decimal(json, "$.lotes[0].quantidade")).isEqualByComparingTo("100");

        // Confere que persistiu (nova leitura)
        json = corpo(getAuth("/insumos/" + insumoId, u.token()).andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(1);
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("100");
    }
}
