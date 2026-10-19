package com.gastrofy.gastrofyapi.integration;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Usuário A não enxerga nem altera nada do usuário B: tudo responde 404.
 */
class IsolamentoUsuarioTest extends IntegrationTestBase {

    @Test
    void usuarioANaoAcessaRecursosDoUsuarioB() throws Exception {
        UsuarioLogado a = criarUsuarioVerificado();
        UsuarioLogado b = criarUsuarioVerificado();

        long insumoB = criarInsumo(b.token(), "1", "1000", LocalDate.now().plusDays(30));
        long receitaB = criarReceita(b.token(), "Receita B", itemReceita(insumoB, "100"));
        long embalagemB = criarEmbalagem(b.token(), 1, 10);
        long produtoB = criarProduto(b.token(), receitaB, "");
        long clienteB = criarCliente(b.token());
        long pedidoB = criarPedido(b.token(), clienteB, produtoB, "1");

        getAuth("/insumos/" + insumoB, a.token()).andExpect(status().isNotFound());
        getAuth("/receitas/" + receitaB, a.token()).andExpect(status().isNotFound());
        getAuth("/produtos/" + produtoB, a.token()).andExpect(status().isNotFound());
        getAuth("/embalagens/" + embalagemB, a.token()).andExpect(status().isNotFound());
        getAuth("/clientes/" + clienteB, a.token()).andExpect(status().isNotFound());
        getAuth("/pedidos/" + pedidoB, a.token()).andExpect(status().isNotFound());

        // Escrita também é bloqueada
        deleteAuth("/insumos/" + insumoB, a.token()).andExpect(status().isNotFound());
        deleteAuth("/clientes/" + clienteB, a.token()).andExpect(status().isNotFound());
        postAuth("/produtos/" + produtoB + "/produzir?quantidade=1", a.token(), null)
                .andExpect(status().isNotFound());
        postAuth("/pedidos/" + pedidoB + "/produzir", a.token(), null)
                .andExpect(status().isNotFound());

        // Receita de A não pode usar insumo de B
        postAuth("/receitas", a.token(), receitaJson("Receita A", itemReceita(insumoB, "10")))
                .andExpect(status().isNotFound());

        // B continua vendo os próprios dados
        getAuth("/insumos/" + insumoB, b.token()).andExpect(status().isOk());
        getAuth("/clientes/" + clienteB, b.token()).andExpect(status().isOk());
    }
}
