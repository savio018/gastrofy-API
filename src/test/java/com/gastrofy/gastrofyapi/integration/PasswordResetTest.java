package com.gastrofy.gastrofyapi.integration;

import com.gastrofy.gastrofyapi.model.PasswordResetToken;
import com.gastrofy.gastrofyapi.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordResetTest extends IntegrationTestBase {

    private static final String SENHA_NOVA = "NovaSenha1";

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    private ResultActions forgot(String email) throws Exception {
        return mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s"}
                        """.formatted(email)));
    }

    private ResultActions reset(String token, String novaSenha) throws Exception {
        return mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"token":"%s","novaSenha":"%s"}
                        """.formatted(token, novaSenha)));
    }

    private String capturarTokenReset(String email) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).enviarResetSenha(eq(email), captor.capture());
        return captor.getValue();
    }

    private List<PasswordResetToken> tokensDo(String email) {
        return resetTokenRepository.findAll().stream()
                .filter(t -> t.getUsuario().getEmail().equals(email))
                .toList();
    }

    private String pedirReset(String email) throws Exception {
        forgot(email).andExpect(status().isNoContent());
        return capturarTokenReset(email);
    }

    @Test
    void forgot_emailInexistente_204ENuncaEnvia() throws Exception {
        forgot(emailUnico()).andExpect(status().isNoContent());

        verify(emailService, never()).enviarResetSenha(anyString(), anyString());
    }

    @Test
    void forgot_emailExistente_204EEnviaUmEmailComToken() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        String token = pedirReset(u.email());

        assertThat(token).isNotBlank();
        assertThat(tokensDo(u.email())).extracting(PasswordResetToken::getToken).containsExactly(token);
    }

    @Test
    void forgot_segundoPedidoLogoEmSeguida_204ESoUmEmail() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();

        forgot(u.email()).andExpect(status().isNoContent());
        forgot(u.email()).andExpect(status().isNoContent());

        verify(emailService, times(1)).enviarResetSenha(eq(u.email()), any());
        assertThat(tokensDo(u.email())).hasSize(1);
    }

    @Test
    void forgot_depoisDoIntervaloMinimo_geraNovoTokenEInvalidaOAnterior() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        String primeiro = pedirReset(u.email());

        // Simula que o primeiro token foi emitido há 2 minutos
        PasswordResetToken salvo = resetTokenRepository.findByToken(primeiro).orElseThrow();
        salvo.setExpirationDate(LocalDateTime.now().plusMinutes(58));
        resetTokenRepository.save(salvo);

        forgot(u.email()).andExpect(status().isNoContent());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(2)).enviarResetSenha(eq(u.email()), captor.capture());
        String segundo = captor.getAllValues().get(1);
        assertThat(segundo).isNotEqualTo(primeiro);

        reset(primeiro, SENHA_NOVA).andExpect(status().isBadRequest());
        reset(segundo, SENHA_NOVA).andExpect(status().isNoContent());
    }

    @Test
    void reset_valido_204_senhaNovaFunciona_antigaFalha_eNaoSobraToken() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        String token = pedirReset(u.email());

        reset(token, SENHA_NOVA).andExpect(status().isNoContent());

        tentarLogin(u.email(), SENHA_NOVA).andExpect(status().isOk());
        tentarLogin(u.email(), SENHA_PADRAO).andExpect(status().isBadRequest());
        assertThat(tokensDo(u.email())).isEmpty();
    }

    @Test
    void reset_mesmoTokenDeNovo_400() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        String token = pedirReset(u.email());

        reset(token, SENHA_NOVA).andExpect(status().isNoContent());

        reset(token, "OutraSenha2")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token de redefinição inválido"));
        tentarLogin(u.email(), SENHA_NOVA).andExpect(status().isOk());
    }

    @Test
    void reset_senhaFraca_400_eTokenContinuaValido() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        String token = pedirReset(u.email());

        reset(token, "fraca").andExpect(status().isBadRequest());
        reset(token, "semnumeroMaiuscula").andExpect(status().isBadRequest());

        tentarLogin(u.email(), SENHA_PADRAO).andExpect(status().isOk());
        assertThat(tokensDo(u.email())).hasSize(1);

        reset(token, SENHA_NOVA).andExpect(status().isNoContent());
    }

    @Test
    void reset_tokenInexistente_400() throws Exception {
        reset("token-que-nao-existe", SENHA_NOVA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token de redefinição inválido"));
    }

    @Test
    void reset_tokenExpirado_400_eTokenEApagado() throws Exception {
        UsuarioLogado u = criarUsuarioVerificado();
        String token = pedirReset(u.email());

        PasswordResetToken salvo = resetTokenRepository.findByToken(token).orElseThrow();
        salvo.setExpirationDate(LocalDateTime.now().minusMinutes(1));
        resetTokenRepository.save(salvo);

        reset(token, SENHA_NOVA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("expirado")));

        tentarLogin(u.email(), SENHA_PADRAO).andExpect(status().isOk());
        assertThat(resetTokenRepository.findByToken(token)).isEmpty();
    }
}
