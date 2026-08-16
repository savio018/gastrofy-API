package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LimpezaService {

    private final UsuarioRepository usuarioRepository;

    @Scheduled(cron = "0 0 3 * * *") // todo dia às 3h da manhã
    @Transactional
    public void deletarUsuariosNaoVerificados() {
        LocalDate limite = LocalDate.now().minusDays(2); // 48 horas
        usuarioRepository.deletarUsuariosNaoVerificadosAntesDe(limite);
        System.out.println("✅ Limpeza executada: usuários não verificados removidos.");
    }
}
