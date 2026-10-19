package com.gastrofy.gastrofyapi.integration;

import com.gastrofy.gastrofyapi.service.EmailService;
import com.jayway.jsonpath.JsonPath;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base dos testes de integração: contexto completo, MockMvc com a cadeia de
 * segurança real e H2 em memória.
 *
 * spring.config.location aponta só para application-test.properties, então o
 * application.properties local nunca é carregado.
 *
 * NÃO usar @Transactional aqui nem nas subclasses: isso manteria a sessão do
 * Hibernate aberta durante o teste e esconderia LazyInitializationException.
 * Como o banco é compartilhado entre os testes, cada teste cria seus próprios
 * usuários com e-mail único.
 */
@SpringBootTest(properties = "spring.config.location=classpath:/application-test.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    protected static final String SENHA_PADRAO = "Senha123";

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected EmailService emailService;

    protected record UsuarioLogado(String email, String token) {
    }

    // ---------- usuário / autenticação ----------

    protected String emailUnico() {
        return "user-" + UUID.randomUUID() + "@teste.com";
    }

    protected ResultActions cadastrar(String email, String senha) throws Exception {
        return mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Usuario Teste","email":"%s","senha":"%s"}
                        """.formatted(email, senha)));
    }

    /** Lê o token que o cadastro "enviou" pelo EmailService mockado. */
    protected String capturarTokenVerificacao(String email) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(emailService, atLeastOnce()).enviarVerificacaoEmail(eq(email), captor.capture());
        return captor.getValue();
    }

    protected void verificarEmail(String email) throws Exception {
        mockMvc.perform(get("/auth/verify-email").param("token", capturarTokenVerificacao(email)))
                .andExpect(status().isNoContent());
    }

    protected ResultActions tentarLogin(String email, String senha) throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","senha":"%s"}
                        """.formatted(email, senha)));
    }

    protected String login(String email, String senha) throws Exception {
        String json = corpo(tentarLogin(email, senha).andExpect(status().isOk()));
        return JsonPath.read(json, "$.token");
    }

    /** Cadastra, verifica o e-mail e faz login. */
    protected UsuarioLogado criarUsuarioVerificado() throws Exception {
        String email = emailUnico();
        cadastrar(email, SENHA_PADRAO).andExpect(status().isOk());
        verificarEmail(email);
        return new UsuarioLogado(email, login(email, SENHA_PADRAO));
    }

    // ---------- requisições autenticadas ----------

    protected ResultActions getAuth(String url, String token) throws Exception {
        return mockMvc.perform(comToken(get(url), token));
    }

    protected ResultActions postAuth(String url, String token, String body) throws Exception {
        return mockMvc.perform(comCorpo(comToken(post(url), token), body));
    }

    protected ResultActions putAuth(String url, String token, String body) throws Exception {
        return mockMvc.perform(comCorpo(comToken(put(url), token), body));
    }

    protected ResultActions patchAuth(String url, String token, String body) throws Exception {
        return mockMvc.perform(comCorpo(comToken(patch(url), token), body));
    }

    protected ResultActions deleteAuth(String url, String token) throws Exception {
        return mockMvc.perform(comToken(delete(url), token));
    }

    private MockHttpServletRequestBuilder comToken(MockHttpServletRequestBuilder req, String token) {
        return req.header("Authorization", "Bearer " + token);
    }

    private MockHttpServletRequestBuilder comCorpo(MockHttpServletRequestBuilder req, String body) {
        return body == null ? req : req.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    // ---------- leitura de respostas ----------

    protected static String corpo(ResultActions result) throws Exception {
        return result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    protected static BigDecimal decimal(String json, String path) {
        Object valor = JsonPath.read(json, path);
        return new BigDecimal(valor.toString());
    }

    protected static long id(String json) {
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }

    // ---------- criação de dados pela API ----------

    /** Cria insumo; o primeiro lote fica com quantidadeCompra x conteudoUnidadeCompra. */
    protected long criarInsumo(String token, String quantidadeCompra, String conteudo, LocalDate validade,
                               String estoqueMinimo, String estoqueCritico) throws Exception {
        String body = """
                {"nome":"Insumo %s","dataValidade":"%s","quantidadeCompra":%s,
                 "unidadeConsumo":"g","unidadeCompra":"kg","conteudoUnidadeCompra":%s,
                 "precoUnidadeCompra":10.00,"estoqueMinimo":%s,"estoqueCritico":%s}
                """.formatted(UUID.randomUUID(), validade, quantidadeCompra, conteudo,
                estoqueMinimo, estoqueCritico);
        return id(corpo(postAuth("/insumos", token, body).andExpect(status().isOk())));
    }

    protected long criarInsumo(String token, String quantidadeCompra, String conteudo, LocalDate validade)
            throws Exception {
        return criarInsumo(token, quantidadeCompra, conteudo, validade, "null", "null");
    }

    protected String reporLotes(String token, long insumoId, String lotesJson) throws Exception {
        return corpo(patchAuth("/insumos/" + insumoId + "/estoque", token, """
                {"lotes":[%s]}
                """.formatted(lotesJson)).andExpect(status().isOk()));
    }

    protected static String lote(String quantidade, LocalDate validade) {
        return """
                {"quantidade":%s,"dataValidade":"%s"}""".formatted(quantidade, validade);
    }

    protected static String receitaJson(String nome, String itensJson) {
        return """
                {"nome":"%s","rendimento":1,"unidadeRendimento":"un","insumos":[%s]}
                """.formatted(nome, itensJson);
    }

    protected static String itemReceita(long insumoId, String quantidade) {
        return """
                {"insumoId":%d,"quantidadeUtilizada":%s}""".formatted(insumoId, quantidade);
    }

    protected long criarReceita(String token, String nome, String itensJson) throws Exception {
        return id(corpo(postAuth("/receitas", token, receitaJson(nome, itensJson))
                .andExpect(status().isOk())));
    }

    protected long criarEmbalagem(String token, int quantidadeCompra, int conteudo) throws Exception {
        String body = """
                {"nome":"Caixa","quantidadeCompra":%d,"unidadeCompra":"pacote",
                 "conteudoUnidadeCompra":%d,"precoUnidadeCompra":2.00}
                """.formatted(quantidadeCompra, conteudo);
        return id(corpo(postAuth("/embalagens", token, body).andExpect(status().isOk())));
    }

    protected long criarProduto(String token, long receitaId, String embalagensJson) throws Exception {
        String body = """
                {"nome":"Produto Teste","receitaId":%d,"quantidadeReceita":1,"precoVenda":50.00,
                 "embalagens":[%s]}
                """.formatted(receitaId, embalagensJson);
        return id(corpo(postAuth("/produtos", token, body).andExpect(status().isOk())));
    }

    protected long criarCliente(String token) throws Exception {
        return id(corpo(postAuth("/clientes", token, """
                {"nome":"Cliente Teste"}
                """).andExpect(status().isOk())));
    }

    protected long criarPedido(String token, long clienteId, long produtoId, String quantidade) throws Exception {
        String body = """
                {"clienteId":%d,"itens":[{"produtoId":%d,"quantidade":%s}]}
                """.formatted(clienteId, produtoId, quantidade);
        return id(corpo(postAuth("/pedidos", token, body).andExpect(status().isOk())));
    }
}
