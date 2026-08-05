package com.gastrofy.gastrofyapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarVerificacaoEmail(String destinatario, String token) {
        String link = "http://localhost:8080/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("saviofreitas017@gmail.com");
        message.setTo(destinatario);
        message.setSubject("Gastrofy - Confirme seu email");
        message.setText(
                "Olá!\n\n" +
                        "Obrigado por se cadastrar no Gastrofy.\n\n" +
                        "Clique no link abaixo para confirmar seu email:\n\n" +
                        link + "\n\n" +
                        "Este link expira em 24 horas.\n\n" +
                        "Se você não criou uma conta, ignore este email.\n\n" +
                        "Equipe Gastrofy"
        );

        mailSender.send(message);
    }

    public void enviarResetSenha(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("saviofreitas017@gmail.com");
        message.setTo(destinatario);
        message.setSubject("Gastrofy - Redefinição de senha");
        message.setText(
                "Olá!\n\n" +
                        "Recebemos uma solicitação para redefinir sua senha.\n\n" +
                        "Seu token de redefinição é:\n\n" +
                        token + "\n\n" +
                        "Este token expira em 1 hora.\n\n" +
                        "Se você não solicitou a redefinição, ignore este email.\n\n" +
                        "Equipe Gastrofy"
        );

        mailSender.send(message);
    }
}