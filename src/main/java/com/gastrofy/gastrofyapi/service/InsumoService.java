package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.InsumoResponseDTO;
import com.gastrofy.gastrofyapi.dto.InsumoUpdateRequestDTO;
import com.gastrofy.gastrofyapi.dto.LoteInsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.LoteInsumoResponseDTO;
import com.gastrofy.gastrofyapi.dto.ReporEstoqueRequestDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.LoteInsumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Transactional
    public InsumoResponseDTO criar(InsumoRequestDTO dto, Usuario usuario) {
        Insumo insumo = new Insumo();
        insumo.setNome(dto.getNome());
        insumo.setUnidadeCompra(dto.getUnidadeCompra());
        insumo.setUnidadeConsumo(dto.getUnidadeConsumo());
        insumo.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        insumo.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        insumo.setEstoqueMinimo(dto.getEstoqueMinimo());
        insumo.setEstoqueCritico(dto.getEstoqueCritico());
        insumo.setDiasAvisoValidade(dto.getDiasAvisoValidade());
        insumo.setUsuario(usuario);

        // Cria o primeiro lote junto com o insumo
        BigDecimal quantidadeLote = dto.getQuantidadeCompra().multiply(dto.getConteudoUnidadeCompra());
        LoteInsumo primeiroLote = new LoteInsumo();
        primeiroLote.setInsumo(insumo);
        primeiroLote.setQuantidade(quantidadeLote);
        primeiroLote.setDataValidade(dto.getDataValidade());
        primeiroLote.setDataCompra(LocalDate.now());
        insumo.getLotes().add(primeiroLote);

        Insumo salvo = insumoRepository.save(insumo);
        return converterParaResponseDTO(salvo);
    }

    @Transactional
    public List<InsumoResponseDTO> listar(Usuario usuario) {
        return insumoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InsumoResponseDTO reporEstoque(Long id, ReporEstoqueRequestDTO dto, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));

        for (LoteInsumoRequestDTO loteDto : dto.getLotes()) {
            LoteInsumo novoLote = new LoteInsumo();
            novoLote.setInsumo(insumo);
            novoLote.setQuantidade(loteDto.getQuantidade());
            novoLote.setDataValidade(loteDto.getDataValidade());
            novoLote.setDataCompra(LocalDate.now());
            insumo.getLotes().add(novoLote);
        }

        Insumo atualizado = insumoRepository.save(insumo);
        return converterParaResponseDTO(atualizado);
    }

    /**
     * Desconta uma quantidade do estoque do insumo seguindo FEFO
     * (First Expire, First Out) — sempre consome primeiro o lote
     * que vence mais cedo. Lotes que chegam a zero são removidos.
     * Lança RegraNegocioException se o estoque total for insuficiente.
     */
    @Transactional
    public void descontarEstoque(Insumo insumo, BigDecimal quantidadeNecessaria) {
        BigDecimal estoqueTotal = calcularEstoqueTotal(insumo);

        if (estoqueTotal.compareTo(quantidadeNecessaria) < 0) {
            throw new RegraNegocioException("Estoque insuficiente para o insumo: " + insumo.getNome());
        }

        List<LoteInsumo> lotesOrdenados = insumo.getLotes().stream()
                .sorted(Comparator.comparing(LoteInsumo::getDataValidade))
                .collect(Collectors.toList());

        BigDecimal restante = quantidadeNecessaria;

        for (LoteInsumo lote : lotesOrdenados) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;

            if (lote.getQuantidade().compareTo(restante) <= 0) {
                restante = restante.subtract(lote.getQuantidade());
                insumo.getLotes().remove(lote);
            } else {
                lote.setQuantidade(lote.getQuantidade().subtract(restante));
                restante = BigDecimal.ZERO;
            }
        }
    }

    private BigDecimal calcularEstoqueTotal(Insumo insumo) {
        return insumo.getLotes().stream()
                .map(LoteInsumo::getQuantidade)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public InsumoResponseDTO buscarPorId(Long id, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));
        return converterParaResponseDTO(insumo);
    }

    @Transactional
    public InsumoResponseDTO atualizar(Long id, InsumoUpdateRequestDTO dto, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));

        insumo.setNome(dto.getNome());
        insumo.setUnidadeCompra(dto.getUnidadeCompra());
        insumo.setUnidadeConsumo(dto.getUnidadeConsumo());
        insumo.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        insumo.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        insumo.setEstoqueMinimo(dto.getEstoqueMinimo());
        insumo.setEstoqueCritico(dto.getEstoqueCritico());
        insumo.setDiasAvisoValidade(dto.getDiasAvisoValidade());

        Insumo atualizado = insumoRepository.save(insumo);
        return converterParaResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));
        insumoRepository.delete(insumo);
    }
    @Transactional
    public InsumoResponseDTO corrigirEstoque(Long id, BigDecimal quantidadePerdida, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", id));

        if (quantidadePerdida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("A quantidade a corrigir deve ser maior que zero");
        }

        descontarEstoque(insumo, quantidadePerdida);

        Insumo atualizado = insumoRepository.save(insumo);
        return converterParaResponseDTO(atualizado);
    }
    @Transactional
    public InsumoResponseDTO excluirLote(Long insumoId, Long loteId, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(insumoId, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", insumoId));

        LoteInsumo lote = insumo.getLotes().stream()
                .filter(l -> l.getId().equals(loteId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote", loteId));

        insumo.getLotes().remove(lote);

        Insumo atualizado = insumoRepository.save(insumo);
        return converterParaResponseDTO(atualizado);
    }
    @Transactional
    public InsumoResponseDTO editarLote(Long insumoId, Long loteId, BigDecimal novaQuantidade, Usuario usuario) {
        Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(insumoId, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", insumoId));

        if (novaQuantidade.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("A quantidade do lote não pode ser negativa");
        }

        LoteInsumo lote = insumo.getLotes().stream()
                .filter(l -> l.getId().equals(loteId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote", loteId));

        if (novaQuantidade.compareTo(BigDecimal.ZERO) == 0) {
            insumo.getLotes().remove(lote);
        } else {
            lote.setQuantidade(novaQuantidade);
        }

        Insumo atualizado = insumoRepository.save(insumo);
        return converterParaResponseDTO(atualizado);
    }

    private InsumoResponseDTO converterParaResponseDTO(Insumo insumo) {
        InsumoResponseDTO dto = new InsumoResponseDTO();
        dto.setId(insumo.getId());
        dto.setNome(insumo.getNome());
        dto.setEstoqueTotal(calcularEstoqueTotal(insumo));
        dto.setUnidadeConsumo(insumo.getUnidadeConsumo());
        dto.setUnidadeCompra(insumo.getUnidadeCompra());
        dto.setConteudoUnidadeCompra(insumo.getConteudoUnidadeCompra());
        dto.setPrecoUnidadeCompra(insumo.getPrecoUnidadeCompra());
        dto.setEstoqueMinimo(insumo.getEstoqueMinimo());
        dto.setEstoqueCritico(insumo.getEstoqueCritico());
        dto.setDiasAvisoValidade(insumo.getDiasAvisoValidade());

        List<LoteInsumoResponseDTO> lotesDTO = insumo.getLotes().stream()
                .sorted(Comparator.comparing(LoteInsumo::getDataValidade))
                .map(lote -> {
                    LoteInsumoResponseDTO l = new LoteInsumoResponseDTO();
                    l.setId(lote.getId());
                    l.setQuantidade(lote.getQuantidade());
                    l.setDataValidade(lote.getDataValidade());
                    l.setDataCompra(lote.getDataCompra());
                    return l;
                })
                .collect(Collectors.toList());
        dto.setLotes(lotesDTO);

        return dto;
    }
}