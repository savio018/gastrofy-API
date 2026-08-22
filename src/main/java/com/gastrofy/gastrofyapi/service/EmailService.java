package com.gastrofy.gastrofyapi.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${app.base.url}")
    private String baseUrl;

    private final OkHttpClient client = new OkHttpClient();

    public void enviarVerificacaoEmail(String destinatario, String token) {
        String link = baseUrl + "/auth/verify-email?token=" + token;

        String corpo = "Olá!<br><br>" +
                "Obrigado por se cadastrar no Gastrofy.<br><br>" +
                "Clique no link abaixo para confirmar seu email:<br><br>" +
                "<a href=\"" + link + "\">" + link + "</a><br><br>" +
                "Este link expira em 24 horas.<br><br>" +
                "Se você não criou uma conta, ignore este email.<br><br>" +
                "Equipe Gastrofy";

        enviar(destinatario, "Gastrofy - Confirme seu email", corpo);
    }

    public void enviarResetSenha(String destinatario, String token) {
        String corpo = "Olá!<br><br>" +
                "Recebemos uma solicitação para redefinir sua senha.<br><br>" +
                "Seu token de redefinição é:<br><br>" +
                "<strong>" + token + "</strong><br><br>" +
                "Este token expira em 1 hora.<br><br>" +
                "Se você não solicitou a redefinição, ignore este email.<br><br>" +
                "Equipe Gastrofy";

        enviar(destinatario, "Gastrofy - Redefinição de senha", corpo);
    }

    private void enviar(String destinatario, String assunto, String corpoHtml) {
        String json = String.format(
                "{\"from\":\"Gastrofy <naoresponda@usegastrofy.com>\",\"to\":[\"%s\"],\"subject\":\"%s\",\"html\":\"%s\"}",
                destinatario, assunto, corpoHtml.replace("\"", "\\\"")
        );

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.resend.com/emails")
                .addHeader("Authorization", "Bearer " + resendApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Falha ao enviar email: " + response.code() + " - " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao conectar com o serviço de email", e);
        }
    }
}