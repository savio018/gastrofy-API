package com.gastrofy.gastrofyapi.config;

import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioTesteRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "teste@gastrofy.com";

        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNome("Usuário Teste");
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode("123456"));
            usuario.setEmailVerificado(true);

            usuarioRepository.save(usuario);

            System.out.println("Usuário de teste criado com sucesso.");
        }
    }
}
