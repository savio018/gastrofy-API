package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.UsuarioRequestDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional(rollbackFor = Exception.class)
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com esse email");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario salvo = usuarioRepository.save(usuario);

        try {
            emailVerificationService.criarTokenParaUsuario(salvo);
        } catch (Exception e) {
            logger.error("ERRO AO ENVIAR EMAIL: {}", e.getMessage(), e);
            throw new RegraNegocioException(
                    "Não foi possível enviar o email de verificação. " +
                            "Verifique se o endereço de email existe e tente novamente."
            );
        }

        return new UsuarioResponseDTO(
                salvo.getIdUsuario(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getDataCriacao()
        );
    }

    public UsuarioResponseDTO buscarPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCriacao()
        );
    }

    public void deletar(Integer id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario atualizado = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(
                atualizado.getIdUsuario(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getDataCriacao()
        );
    }
}