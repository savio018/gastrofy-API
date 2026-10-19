package com.gastrofy.gastrofyapi.integration;

import com.gastrofy.gastrofyapi.model.EmailVerificationToken;
import com.gastrofy.gastrofyapi.repository.EmailVerificationTokenRepository;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailVerificacaoTest extends IntegrationTestBase {

    @Autowired
    private EmailVerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private ResultActions verificar(String token) throws Exception {
        return mockMvc.perform(get("/auth/verify-email").param("token", token));
    }

    private boolean emailVerificado(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow().isEmailVerificado();
    }

    /** Cadastra e devolve o token "enviado" pelo EmailService mockado. */
    private String cadastrarECapturarToken(String email) throws Exception {
        cadastrar(email, SENHA_PADRAO).andExpect(status().isOk());
        return capturarTokenVerificacao(email);
    }

    @Test
    void tokenValido_204_eMarcaEmailVerificado() throws Exception {
        String email = emailUnico();
        String token = cadastrarECapturarToken(email);
        assertThat(emailVerificado(email)).isFalse();

        verificar(token).andExpect(status().isNoContent());

        assertThat(emailVerificado(email)).isTrue();
        assertThat(verificationTokenRepository.findByToken(token)).isEmpty();
    }

    @Test
    void mesmoTokenDeNovo_400() throws Exception {
        String email = emailUnico();
        String token = cadastrarECapturarToken(email);
        verificar(token).andExpect(status().isNoContent());

        verificar(token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token de verificação inválido"));
    }

    @Test
    void tokenInexistente_400() throws Exception {
        verificar("token-que-nao-existe")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token de verificação inválido"));
    }

    @Test
    void tokenExpirado_400_eTokenSomeDoBanco() throws Exception {
        String email = emailUnico();
        String token = cadastrarECapturarToken(email);

        EmailVerificationToken salvo = verificationTokenRepository.findByToken(token).orElseThrow();
        salvo.setExpirationDate(LocalDateTime.now().minusMinutes(1));
        verificationTokenRepository.save(salvo);

        verificar(token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token de verificação expirado. Solicite um novo link."));

        assertThat(verificationTokenRepository.findByToken(token)).isEmpty();
        assertThat(emailVerificado(email)).isFalse();
    }
}
