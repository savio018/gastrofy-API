package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.EmbalagemRequestDTO;
import com.gastrofy.gastrofyapi.dto.EmbalagemResponseDTO;
import com.gastrofy.gastrofyapi.dto.EmbalagemUpdateRequestDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Embalagem;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.EmbalagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbalagemService {

    private final EmbalagemRepository embalagemRepository;

    public EmbalagemService(EmbalagemRepository embalagemRepository) {
        this.embalagemRepository = embalagemRepository;
    }

    public EmbalagemResponseDTO criar(EmbalagemRequestDTO dto, Usuario usuario) {
        Embalagem embalagem = new Embalagem();
        embalagem.setNome(dto.getNome());
        embalagem.setUnidadeCompra(dto.getUnidadeCompra());
        embalagem.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        embalagem.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        embalagem.setEstoqueMinimo(dto.getEstoqueMinimo());
        embalagem.setEstoqueCritico(dto.getEstoqueCritico());

        Integer estoqueCalculado = dto.getQuantidadeCompra() * dto.getConteudoUnidadeCompra();
        embalagem.setEstoqueTotal(estoqueCalculado);
        embalagem.setUsuario(usuario);

        Embalagem salvo = embalagemRepository.save(embalagem);
        return converterParaResponseDTO(salvo);
    }

    public List<EmbalagemResponseDTO> listar(Usuario usuario) {
        return embalagemRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public EmbalagemResponseDTO reporEstoque(Long id, Integer quantidade, Usuario usuario) {
        Embalagem embalagem = embalagemRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Embalagem", id));

        Integer novoEstoque = embalagem.getEstoqueTotal() + quantidade;

        if (novoEstoque < 0) {
            throw new RegraNegocioException("Estoque não pode ficar negativo. Estoque atual: "
                    + embalagem.getEstoqueTotal() + " unidades");
        }

        embalagem.setEstoqueTotal(novoEstoque);
        Embalagem atualizado = embalagemRepository.save(embalagem);
        return converterParaResponseDTO(atualizado);
    }

    public EmbalagemResponseDTO buscarPorId(Long id, Usuario usuario) {
        Embalagem embalagem = embalagemRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Embalagem", id));
        return converterParaResponseDTO(embalagem);
    }

    public EmbalagemResponseDTO atualizar(Long id, EmbalagemUpdateRequestDTO dto, Usuario usuario) {
        Embalagem embalagem = embalagemRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Embalagem", id));

        embalagem.setNome(dto.getNome());
        embalagem.setUnidadeCompra(dto.getUnidadeCompra());
        embalagem.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        embalagem.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        embalagem.setEstoqueMinimo(dto.getEstoqueMinimo());
        embalagem.setEstoqueCritico(dto.getEstoqueCritico());

        Embalagem atualizado = embalagemRepository.save(embalagem);
        return converterParaResponseDTO(atualizado);
    }

    public void deletar(Long id, Usuario usuario) {
        Embalagem embalagem = embalagemRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Embalagem", id));
        embalagemRepository.delete(embalagem);
    }

    private EmbalagemResponseDTO converterParaResponseDTO(Embalagem embalagem) {
        EmbalagemResponseDTO dto = new EmbalagemResponseDTO();
        dto.setId(embalagem.getId());
        dto.setNome(embalagem.getNome());
        dto.setEstoqueTotal(embalagem.getEstoqueTotal());
        dto.setUnidadeCompra(embalagem.getUnidadeCompra());
        dto.setConteudoUnidadeCompra(embalagem.getConteudoUnidadeCompra());
        dto.setPrecoUnidadeCompra(embalagem.getPrecoUnidadeCompra());
        dto.setEstoqueMinimo(embalagem.getEstoqueMinimo());
        dto.setEstoqueCritico(embalagem.getEstoqueCritico());
        return dto;
    }
}
