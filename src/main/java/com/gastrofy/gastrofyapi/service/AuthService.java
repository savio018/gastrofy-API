package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.LoginRequestDTO;
import com.gastrofy.gastrofyapi.dto.LoginResponseDTO;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import com.gastrofy.gastrofyapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Realiza login do usuário e retorna token JWT
     * 
     * @param dto Credenciais do usuário (email e senha)
     * @return LoginResponseDTO contendo o token JWT
     * @throws RegraNegocioException se credenciais inválidas ou email não verificado
     */
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // ✅ Buscar usuário por email
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas"));

        // ✅ Validar senha
        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), usuario.getSenha());
        if (!senhaCorreta) {
            throw new RegraNegocioException("Credenciais inválidas");
        }

        // ✅ Validar email verificado
        if (!usuario.isEmailVerificado()) {
            throw new RegraNegocioException("Email não verificado");
        }

        // ✅ Gerar token JWT
        String token = jwtService.gerarToken(usuario.getEmail());
        return new LoginResponseDTO(token);
    }
}

