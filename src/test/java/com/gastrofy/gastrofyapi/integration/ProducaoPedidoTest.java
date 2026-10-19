package com.gastrofy.gastrofyapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProducaoPedidoTest extends IntegrationTestBase {

    private static final LocalDate HOJE = LocalDate.now();

    @Test
    void produzir_consomeFefoAtravessandoDoisLotes() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        LocalDate vencimentoTardio = HOJE.plusDays(30);
        long farinha = criarInsumo(u.token(), "1", "100", vencimentoTardio); // 100 (+30)
        reporLotes(u.token(), farinha, lote("60", HOJE.plusDays(5)));      // 60 (+5)

        long receita = criarReceita(u.token(), "Pao", itemReceita(farinha, "80"));
        long produto = criarProduto(u.token(), receita, "");

        postAuth("/produtos/" + produto + "/produzir?quantidade=1", u.token(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estoqueProduto").value(1));

        // 80 = 60 do lote (+5), que some, + 20 do lote (+30), que fica com 80
        String json = corpo(getAuth("/insumos/" + farinha, u.token()).andExpect(status().isOk()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(1);
        assertThat((String) JsonPath.read(json, "$.lotes[0].dataValidade")).isEqualTo(vencimentoTardio.toString());
        assertThat(decimal(json, "$.lotes[0].quantidade")).isEqualByComparingTo("80");
    }

    @Test
    void produzir_estoqueInsuficienteDa400_eNadaEDescontado() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        long comEstoque = criarInsumo(u.token(), "1", "1000", HOJE.plusDays(30)); // 1000
        long semEstoque = criarInsumo(u.token(), "1", "10", HOJE.plusDays(30));   // 10

        // O primeiro insumo tem estoque e é descontado antes do segundo falhar
        long receita = criarReceita(u.token(), "Bolo",
                itemReceita(comEstoque, "100") + "," + itemReceita(semEstoque, "50"));
        long produto = criarProduto(u.token(), receita, "");

        postAuth("/produtos/" + produto + "/produzir?quantidade=1", u.token(), null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Estoque insuficiente para o insumo")));

        String json = corpo(getAuth("/insumos/" + comEstoque, u.token()));
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("1000");
        json = corpo(getAuth("/insumos/" + semEstoque, u.token()));
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("10");
        json = corpo(getAuth("/produtos/" + produto, u.token()));
        assertThat(decimal(json, "$.estoqueProduto")).isEqualByComparingTo("0");
    }

    @Test
    void produzir_embalagemInsuficienteBloqueia_eInsumoNaoEDescontado() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        long insumo = criarInsumo(u.token(), "1", "100", HOJE.plusDays(30));
        long receita = criarReceita(u.token(), "Brigadeiro", itemReceita(insumo, "10"));
        long embalagem = criarEmbalagem(u.token(), 1, 1); // estoque 1
        long produto = criarProduto(u.token(), receita, """
                {"embalagemId":%d,"quantidadeUtilizada":2}""".formatted(embalagem));

        postAuth("/produtos/" + produto + "/produzir?quantidade=1", u.token(), null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Estoque insuficiente da embalagem")));

        // O insumo é descontado antes da checagem da embalagem: o rollback tem que desfazer
        String json = corpo(getAuth("/insumos/" + insumo, u.token()));
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("100");
        json = corpo(getAuth("/embalagens/" + embalagem, u.token()));
        assertThat((Integer) JsonPath.read(json, "$.estoqueTotal")).isEqualTo(1);
        json = corpo(getAuth("/produtos/" + produto, u.token()));
        assertThat(decimal(json, "$.estoqueProduto")).isEqualByComparingTo("0");
    }

    @Test
    void pedido_produzirSemEstoqueDoProdutoDa400_eContinuaPendente() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        long insumo = criarInsumo(u.token(), "1", "100", HOJE.plusDays(30));
        long receita = criarReceita(u.token(), "Torta", itemReceita(insumo, "10"));
        long produto = criarProduto(u.token(), receita, ""); // estoqueProduto = 0
        long cliente = criarCliente(u.token());
        long pedido = criarPedido(u.token(), cliente, produto, "2");

        postAuth("/pedidos/" + pedido + "/produzir", u.token(), null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Estoque insuficiente do produto")));

        getAuth("/pedidos/" + pedido, u.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }
}
