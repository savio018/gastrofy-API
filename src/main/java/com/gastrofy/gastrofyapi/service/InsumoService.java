package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.InsumoResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    public InsumoResponseDTO criar(InsumoRequestDTO dto, Usuario usuario) {
        Insumo insumo = new Insumo();
        insumo.setNome(dto.getNome());
        insumo.setUnidadeCompra(dto.getUnidadeCompra());
        insumo.setUnidadeConsumo(dto.getUnidadeConsumo());
        insumo.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        insumo.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        insumo.setDataValidade(dto.getDataValidade());

        BigDecimal estoqueCalculado = dto.getQuantidadeCompra().multiply(dto.getConteudoUnidadeCompra());

        insumo.setEstoqueTotal(estoqueCalculado);
        insumo.setUsuario(usuario);

        Insumo salvo = insumoRepository.save(insumo);
        return converterParaResponseDTO(salvo);
    }

    public List<InsumoResponseDTO> listar(Usuario usuario) {
        return insumoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public InsumoResponseDTO buscarPorId(Long id, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));
        return converterParaResponseDTO(insumo);
    }

    public InsumoResponseDTO atualizar(Long id, InsumoRequestDTO dto, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));

        insumo.setNome(dto.getNome());
        insumo.setUnidadeCompra(dto.getUnidadeCompra());
        insumo.setUnidadeConsumo(dto.getUnidadeConsumo());
        insumo.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        insumo.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        insumo.setDataValidade(dto.getDataValidade());

        BigDecimal estoqueCalculado = dto.getQuantidadeCompra().multiply(dto.getConteudoUnidadeCompra());
        insumo.setEstoqueTotal(estoqueCalculado);

        Insumo atualizado = insumoRepository.save(insumo);
        return converterParaResponseDTO(atualizado);
    }

    public void deletar(Long id, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));
        insumoRepository.delete(insumo);
    }

    private InsumoResponseDTO converterParaResponseDTO(Insumo insumo) {
        InsumoResponseDTO dto = new InsumoResponseDTO();
        dto.setId(insumo.getId());
        dto.setNome(insumo.getNome());
        dto.setDataValidade(insumo.getDataValidade());
        dto.setEstoqueTotal(insumo.getEstoqueTotal());
        dto.setUnidadeConsumo(insumo.getUnidadeConsumo());
        dto.setUnidadeCompra(insumo.getUnidadeCompra());
        dto.setConteudoUnidadeCompra(insumo.getConteudoUnidadeCompra());
        dto.setPrecoUnidadeCompra(insumo.getPrecoUnidadeCompra());
        return dto;
    }

}
