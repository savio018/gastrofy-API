package com.gastrofy.gastrofyapi.integration;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthUsuarioTest extends IntegrationTestBase {

    @Test
    void loginComEmailNaoVerificadoFalha_eDepoisDeVerificarFunciona() throws Exception {
        String email = emailUnico();
        cadastrar(email, SENHA_PADRAO).andExpect(status().isOk());

        tentarLogin(email, SENHA_PADRAO)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email não verificado"));

        verificarEmail(email);

        tentarLogin(email, SENHA_PADRAO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void putMe_ignoraEmailDoCorpo() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        putAuth("/usuarios/me", u.token(), """
                {"nome":"Nome Atualizado","email":"invasor@teste.com"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(u.email()))
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"));

        getAuth("/usuarios/me", u.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(u.email()));

        // O e-mail original continua sendo o de login
        tentarLogin(u.email(), SENHA_PADRAO).andExpect(status().isOk());
    }

    @Test
    void putMe_trocaDeSenhaExigeSenhaAtual() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        putAuth("/usuarios/me", u.token(), """
                {"nome":"Nome Teste","novaSenha":"NovaSenha1"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Senha atual incorreta"));

        putAuth("/usuarios/me", u.token(), """
                {"nome":"Nome Teste","senhaAtual":"Errada123","novaSenha":"NovaSenha1"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Senha atual incorreta"));

        // Nada mudou até aqui
        tentarLogin(u.email(), SENHA_PADRAO).andExpect(status().isOk());

        putAuth("/usuarios/me", u.token(), """
                {"nome":"Nome Teste","senhaAtual":"%s","novaSenha":"NovaSenha1"}
                """.formatted(SENHA_PADRAO))
                .andExpect(status().isOk());

        tentarLogin(u.email(), "NovaSenha1").andExpect(status().isOk());
        tentarLogin(u.email(), SENHA_PADRAO).andExpect(status().isBadRequest());
    }
}
