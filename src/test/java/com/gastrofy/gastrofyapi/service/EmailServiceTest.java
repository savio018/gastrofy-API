package com.gastrofy.gastrofyapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class EmailServiceTest {

    @Test
    void corpoResetSenha_trazLinkDoFrontend_validade_eNaoMostraTokenCru() {
        EmailService emailService = new EmailService();
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:5173");

        String corpo = emailService.montarCorpoResetSenha("abc-123");

        assertThat(corpo).contains("<a href=\"http://localhost:5173/redefinir-senha?token=abc-123\">");
        assertThat(corpo).contains("1 hora");
        // O token só aparece dentro do href, nunca como texto visível
        assertThat(corpo.split("abc-123", -1)).hasSize(2);
        assertThat(corpo).doesNotContain("<strong>");
    }

    @Test
    void corpoVerificacaoEmail_trazLinkDoFrontend_validade_eNaoMostraTokenCru() {
        EmailService emailService = new EmailService();
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:5173");

        String corpo = emailService.montarCorpoVerificacaoEmail("abc-123");

        assertThat(corpo).contains(
                "<a href=\"http://localhost:5173/confirmar-email?token=abc-123\">Confirmar meu e-mail</a>");
        assertThat(corpo).contains("24 horas");
        // O token só aparece dentro do href, nunca como texto visível
        assertThat(corpo.split("abc-123", -1)).hasSize(2);
    }
}
