package com.gastrofy.gastrofyapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cada regra é testada com 0 e com um valor negativo: a resposta é 400 e
 * nada é gravado.
 */
class ValidacaoValoresPositivosTest extends IntegrationTestBase {

    private static final LocalDate VALIDADE = LocalDate.now().plusDays(30);

    private static String insumoJson(String quantidadeCompra, String conteudo, String preco) {
        return """
                {"nome":"Farinha","dataValidade":"%s","quantidadeCompra":%s,
                 "unidadeConsumo":"g","unidadeCompra":"kg","conteudoUnidadeCompra":%s,
                 "precoUnidadeCompra":%s}
                """.formatted(VALIDADE, quantidadeCompra, conteudo, preco);
    }

    private static String insumoUpdateJson(String conteudo, String preco) {
        return """
                {"nome":"Editado","unidadeConsumo":"g","unidadeCompra":"kg",
                 "conteudoUnidadeCompra":%s,"precoUnidadeCompra":%s}
                """.formatted(conteudo, preco);
    }

    private static String embalagemJson(String quantidadeCompra, String conteudo, String preco) {
        return """
                {"nome":"Caixa","quantidadeCompra":%s,"unidadeCompra":"pacote",
                 "conteudoUnidadeCompra":%s,"precoUnidadeCompra":%s}
                """.formatted(quantidadeCompra, conteudo, preco);
    }

    private static String embalagemUpdateJson(String conteudo, String preco) {
        return """
                {"nome":"Editada","unidadeCompra":"pacote",
                 "conteudoUnidadeCompra":%s,"precoUnidadeCompra":%s}
                """.formatted(conteudo, preco);
    }

    private static String produtoJson(long receitaId, String quantidadeReceita, String precoVenda) {
        return """
                {"nome":"Bolo","receitaId":%d,"quantidadeReceita":%s,"precoVenda":%s}
                """.formatted(receitaId, quantidadeReceita, precoVenda);
    }

    private void assertListaVazia(String url, String token) throws Exception {
        getAuth(url, token).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------- Insumo: criação ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void insumo_quantidadeCompraNaoPositiva_400(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        postAuth("/insumos", u.token(), insumoJson(valor, "1000", "10.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("quantidadeCompra"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("Quantidade de compra deve ser maior que zero"));

        assertListaVazia("/insumos", u.token());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void insumo_conteudoNaoPositivo_400(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        postAuth("/insumos", u.token(), insumoJson("1", valor, "10.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("conteudoUnidadeCompra"));

        assertListaVazia("/insumos", u.token());
    }

    @Test
    void insumo_precoNegativo_400_ePrecoZeroEAceito() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        postAuth("/insumos", u.token(), insumoJson("1", "1000", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].mensagem").value("Preço por unidade de compra não pode ser negativo"));
        assertListaVazia("/insumos", u.token());

        // Brinde/doação: preço zero é válido
        postAuth("/insumos", u.token(), insumoJson("1", "1000", "0"))
                .andExpect(status().isOk());
    }

    // ---------- Insumo: edição ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void insumo_editarComConteudoNaoPositivo_400_eNadaMuda(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "1000", VALIDADE);

        putAuth("/insumos/" + insumo, u.token(), insumoUpdateJson(valor, "10.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("conteudoUnidadeCompra"));

        String json = corpo(getAuth("/insumos/" + insumo, u.token()));
        assertThat(decimal(json, "$.conteudoUnidadeCompra")).isEqualByComparingTo("1000");
        assertThat((String) JsonPath.read(json, "$.nome")).isNotEqualTo("Editado");
    }

    @Test
    void insumo_editarComPrecoNegativo_400_eNadaMuda() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "1000", VALIDADE);

        putAuth("/insumos/" + insumo, u.token(), insumoUpdateJson("1000", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("precoUnidadeCompra"));

        String json = corpo(getAuth("/insumos/" + insumo, u.token()));
        assertThat(decimal(json, "$.precoUnidadeCompra")).isEqualByComparingTo("10");
    }

    // ---------- Lote (PATCH /insumos/{id}/estoque) ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void reporLote_quantidadeNaoPositiva_400_eNadaMuda(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "100", VALIDADE);

        patchAuth("/insumos/" + insumo + "/estoque", u.token(), """
                {"lotes":[%s]}
                """.formatted(lote(valor, VALIDADE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].mensagem").value("Quantidade deve ser maior que zero"));

        String json = corpo(getAuth("/insumos/" + insumo, u.token()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(1);
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("100");
    }

    // ---------- Produto ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void produto_quantidadeReceitaNaoPositiva_400(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "100", VALIDADE);
        long receita = criarReceita(u.token(), "Bolo", itemReceita(insumo, "10"));

        postAuth("/produtos", u.token(), produtoJson(receita, valor, "50.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].mensagem").value("Quantidade da receita deve ser maior que zero"));

        assertListaVazia("/produtos", u.token());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void produto_precoVendaNaoPositivo_400(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "100", VALIDADE);
        long receita = criarReceita(u.token(), "Bolo", itemReceita(insumo, "10"));

        postAuth("/produtos", u.token(), produtoJson(receita, "1", valor))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].mensagem").value("Preço de venda deve ser maior que zero"));

        assertListaVazia("/produtos", u.token());
    }

    // ---------- Embalagem: criação ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void embalagem_quantidadeCompraNaoPositiva_400(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        postAuth("/embalagens", u.token(), embalagemJson(valor, "10", "2.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("quantidadeCompra"));

        assertListaVazia("/embalagens", u.token());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void embalagem_conteudoNaoPositivo_400(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        postAuth("/embalagens", u.token(), embalagemJson("1", valor, "2.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("conteudoUnidadeCompra"));

        assertListaVazia("/embalagens", u.token());
    }

    @Test
    void embalagem_precoNegativo_400_ePrecoZeroEAceito() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        postAuth("/embalagens", u.token(), embalagemJson("1", "10", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("precoUnidadeCompra"));
        assertListaVazia("/embalagens", u.token());

        postAuth("/embalagens", u.token(), embalagemJson("1", "10", "0"))
                .andExpect(status().isOk());
    }

    // ---------- Embalagem: edição ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void embalagem_editarComConteudoNaoPositivo_400_eNadaMuda(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long embalagem = criarEmbalagem(u.token(), 1, 10);

        putAuth("/embalagens/" + embalagem, u.token(), embalagemUpdateJson(valor, "2.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("conteudoUnidadeCompra"));

        getAuth("/embalagens/" + embalagem, u.token())
                .andExpect(jsonPath("$.conteudoUnidadeCompra").value(10))
                .andExpect(jsonPath("$.nome").value("Caixa"));
    }

    @Test
    void embalagem_editarComPrecoNegativo_400_eNadaMuda() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long embalagem = criarEmbalagem(u.token(), 1, 10);

        putAuth("/embalagens/" + embalagem, u.token(), embalagemUpdateJson("10", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("precoUnidadeCompra"));

        String json = corpo(getAuth("/embalagens/" + embalagem, u.token()));
        assertThat(decimal(json, "$.precoUnidadeCompra")).isEqualByComparingTo("2");
    }

    @Test
    void embalagem_patchDeEstoqueContinuaAceitandoNegativo() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long embalagem = criarEmbalagem(u.token(), 1, 10); // estoque 10

        patchAuth("/embalagens/" + embalagem + "/estoque?quantidade=-3", u.token(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estoqueTotal").value(7));
    }

    // ---------- produzir / corrigir-estoque ----------

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void produzir_quantidadeNaoPositiva_400_eNadaMuda(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "100", VALIDADE);
        long receita = criarReceita(u.token(), "Bolo", itemReceita(insumo, "10"));
        long produto = criarProduto(u.token(), receita, "");

        postAuth("/produtos/" + produto + "/produzir?quantidade=" + valor, u.token(), null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A quantidade a produzir deve ser maior que zero"));

        String json = corpo(getAuth("/insumos/" + insumo, u.token()));
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("100");
        json = corpo(getAuth("/produtos/" + produto, u.token()));
        assertThat(decimal(json, "$.estoqueProduto")).isEqualByComparingTo("0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void corrigirEstoque_quantidadeNaoPositiva_400_eNadaMuda(String valor) throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        long insumo = criarInsumo(u.token(), "1", "100", VALIDADE);

        postAuth("/insumos/" + insumo + "/corrigir-estoque?quantidade=" + valor, u.token(), null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A quantidade a corrigir deve ser maior que zero"));

        String json = corpo(getAuth("/insumos/" + insumo, u.token()));
        assertThat((List<?>) JsonPath.read(json, "$.lotes")).hasSize(1);
        assertThat(decimal(json, "$.estoqueTotal")).isEqualByComparingTo("100");
    }
}
