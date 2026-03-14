package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.UsuarioCadastroResponseDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioRequestDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public UsuarioCadastroResponseDTO criar(UsuarioRequestDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario salvo = usuarioRepository.save(usuario);

        String tokenVerificacao = emailVerificationService.criarTokenParaUsuario(salvo);

        UsuarioResponseDTO usuarioResponse = new UsuarioResponseDTO(
                salvo.getIdUsuario(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getDataCriacao()
        );

        return new UsuarioCadastroResponseDTO(usuarioResponse, tokenVerificacao);
    }

    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getIdUsuario(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getDataCriacao()
                ));
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    public void deletar(Integer id) {
        buscarPorId(id);
        usuarioRepository.deleteById(id);
    }

    public Usuario atualizar(Integer id, Usuario usuarioAtualizado) {
        Usuario usuario = buscarPorId(id);

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setSenha(usuarioAtualizado.getSenha());

        return usuarioRepository.save(usuario);
    }
}
