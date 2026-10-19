package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.UsuarioResponseDTO;
import com.gastrofy.gastrofyapi.dto.UsuarioUpdateRequestDTO;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final Integer ID = 1;
    private static final String EMAIL = "usuario@gastrofy.com";
    private static final String HASH_ANTIGO = "hash-antigo";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Usuario original = new Usuario();
        original.setIdUsuario(ID);
        original.setNome("Nome Antigo");
        original.setEmail(EMAIL);
        original.setSenha(HASH_ANTIGO);
        usuario = spy(original);

        when(usuarioRepository.findById(ID)).thenReturn(Optional.of(usuario));
    }

    private UsuarioUpdateRequestDTO dto(String nome, String senhaAtual, String novaSenha) {
        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO();
        dto.setNome(nome);
        dto.setSenhaAtual(senhaAtual);
        dto.setNovaSenha(novaSenha);
        return dto;
    }

    @Test
    void atualizar_soNome_atualizaNomeENaoAlteraSenha() {
        when(usuarioRepository.save(any(Usuario.class))).then(returnsFirstArg());

        usuarioService.atualizar(ID, dto("Nome Novo", null, null));

        assertThat(usuario.getNome()).isEqualTo("Nome Novo");
        assertThat(usuario.getSenha()).isEqualTo(HASH_ANTIGO);
        verify(usuario, never()).setSenha(any());
        verifyNoInteractions(passwordEncoder);
        verify(usuario, never()).setEmail(any());
    }

    @Test
    void atualizar_novaSenhaSemSenhaAtual_lancaExcecao() {
        assertThatThrownBy(() -> usuarioService.atualizar(ID, dto("Nome Novo", null, "NovaSenha1")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Senha atual incorreta");

        assertThat(usuario.getSenha()).isEqualTo(HASH_ANTIGO);
        verify(usuarioRepository, never()).save(any());
        verify(usuario, never()).setEmail(any());
    }

    @Test
    void atualizar_senhaAtualErrada_lancaExcecao() {
        when(passwordEncoder.matches("SenhaErrada1", HASH_ANTIGO)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.atualizar(ID, dto("Nome Novo", "SenhaErrada1", "NovaSenha1")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Senha atual incorreta");

        assertThat(usuario.getSenha()).isEqualTo(HASH_ANTIGO);
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
        verify(usuario, never()).setEmail(any());
    }

    @Test
    void atualizar_senhaAtualCorreta_regravaSenhaComEncoder() {
        when(usuarioRepository.save(any(Usuario.class))).then(returnsFirstArg());
        when(passwordEncoder.matches("SenhaAtual1", HASH_ANTIGO)).thenReturn(true);
        when(passwordEncoder.encode("NovaSenha1")).thenReturn("hash-novo");

        usuarioService.atualizar(ID, dto("Nome Novo", "SenhaAtual1", "NovaSenha1"));

        assertThat(usuario.getSenha()).isEqualTo("hash-novo");
        assertThat(usuario.getNome()).isEqualTo("Nome Novo");
        verify(passwordEncoder).encode("NovaSenha1");
        verify(usuarioRepository).save(usuario);
        verify(usuario, never()).setEmail(any());
    }

    @Test
    void atualizar_nuncaAlteraEmail() {
        when(usuarioRepository.save(any(Usuario.class))).then(returnsFirstArg());
        when(passwordEncoder.matches("SenhaAtual1", HASH_ANTIGO)).thenReturn(true);
        when(passwordEncoder.encode("NovaSenha1")).thenReturn("hash-novo");

        UsuarioResponseDTO resposta = usuarioService.atualizar(ID, dto("Nome Novo", "SenhaAtual1", "NovaSenha1"));

        verify(usuario, never()).setEmail(any());
        assertThat(usuario.getEmail()).isEqualTo(EMAIL);
        assertThat(resposta.getEmail()).isEqualTo(EMAIL);
    }
}
