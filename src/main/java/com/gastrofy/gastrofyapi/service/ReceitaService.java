package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.ReceitaInsumoRequestDTO;
import com.gastrofy.gastrofyapi.dto.ReceitaInsumoResponseDTO;
import com.gastrofy.gastrofyapi.dto.ReceitaRequestDTO;
import com.gastrofy.gastrofyapi.dto.ReceitaResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.Receita;
import com.gastrofy.gastrofyapi.model.ReceitaInsumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import com.gastrofy.gastrofyapi.repository.ReceitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final InsumoRepository insumoRepository;

    public ReceitaService(ReceitaRepository receitaRepository, InsumoRepository insumoRepository) {
        this.receitaRepository = receitaRepository;
        this.insumoRepository = insumoRepository;
    }

    @Transactional
    public ReceitaResponseDTO criar(ReceitaRequestDTO dto, Usuario usuario, boolean forcarCriacao) {
        if (!forcarCriacao) {
            verificarDuplicidade(dto, usuario);
        }

        Receita receita = new Receita();
        receita.setNome(dto.getNome());
        receita.setRendimento(dto.getRendimento());
        receita.setUnidadeRendimento(dto.getUnidadeRendimento());
        receita.setModoPreparo(dto.getModoPreparo());
        receita.setUsuario(usuario);

        List<ReceitaInsumo> itens = montarItens(dto.getInsumos(), receita, usuario);
        receita.setInsumos(itens);

        BigDecimal custoTotal = calcularCustoTotal(itens);
        receita.setCustoTotal(custoTotal);

        Receita salvo = receitaRepository.save(receita);
        return converterParaResponseDTO(salvo);
    }

    @Transactional
    public List<ReceitaResponseDTO> listar(Usuario usuario) {
        return receitaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReceitaResponseDTO buscarPorId(Long id, Usuario usuario) {
        Receita receita = receitaRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", id));
        return converterParaResponseDTO(receita);
    }

    @Transactional
    public ReceitaResponseDTO atualizar(Long id, ReceitaRequestDTO dto, Usuario usuario) {
        Receita receita = receitaRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", id));

        receita.setNome(dto.getNome());
        receita.setRendimento(dto.getRendimento());
        receita.setUnidadeRendimento(dto.getUnidadeRendimento());
        receita.setModoPreparo(dto.getModoPreparo());

        receita.getInsumos().clear();
        List<ReceitaInsumo> itens = montarItens(dto.getInsumos(), receita, usuario);
        receita.getInsumos().addAll(itens);

        BigDecimal custoTotal = calcularCustoTotal(itens);
        receita.setCustoTotal(custoTotal);

        Receita atualizado = receitaRepository.save(receita);
        return converterParaResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id, Usuario usuario) {
        Receita receita = receitaRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", id));
        receitaRepository.delete(receita);
    }

    private void verificarDuplicidade(ReceitaRequestDTO dto, Usuario usuario) {
        List<Receita> receitasComMesmoNome = receitaRepository
                .findByUsuarioIdUsuarioAndNomeIgnoreCase(usuario.getIdUsuario(), dto.getNome());

        if (receitasComMesmoNome.isEmpty()) {
            return;
        }

        Map<Long, BigDecimal> ingredientesNovos = mapearIngredientes(dto.getInsumos());

        for (Receita existente : receitasComMesmoNome) {
            Map<Long, BigDecimal> ingredientesExistentes = existente.getInsumos().stream()
                    .collect(Collectors.toMap(
                            item -> item.getInsumo().getId(),
                            ReceitaInsumo::getQuantidadeUtilizada
                    ));

            if (ingredientesSaoIguais(ingredientesNovos, ingredientesExistentes)) {
                throw new RegraNegocioException(
                        "Já existe uma receita chamada '" + existente.getNome() +
                                "' com exatamente os mesmos ingredientes e quantidades. " +
                                "Confirme se deseja criar mesmo assim."
                );
            }
        }

        throw new RegraNegocioException(
                "Já existe uma receita chamada '" + dto.getNome() +
                        "'. Confirme se deseja criar mesmo assim."
        );
    }

    private boolean ingredientesSaoIguais(Map<Long, BigDecimal> a, Map<Long, BigDecimal> b) {
        if (!a.keySet().equals(b.keySet())) {
            return false;
        }

        for (Long insumoId : a.keySet()) {
            if (a.get(insumoId).compareTo(b.get(insumoId)) != 0) {
                return false;
            }
        }

        return true;
    }

    private Map<Long, BigDecimal> mapearIngredientes(List<ReceitaInsumoRequestDTO> insumos) {
        Map<Long, BigDecimal> mapa = new HashMap<>();
        for (ReceitaInsumoRequestDTO item : insumos) {
            mapa.put(item.getInsumoId(), item.getQuantidadeUtilizada());
        }
        return mapa;
    }
    private List<ReceitaInsumo> montarItens(List<ReceitaInsumoRequestDTO> dtos, Receita receita, Usuario usuario) {
        return dtos.stream().map(dto -> {
            Insumo insumo = insumoRepository.findByIdAndUsuarioIdUsuario(dto.getInsumoId(), usuario.getIdUsuario())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo", dto.getInsumoId()));

            ReceitaInsumo item = new ReceitaInsumo();
            item.setReceita(receita);
            item.setInsumo(insumo);
            item.setQuantidadeUtilizada(dto.getQuantidadeUtilizada());
            return item;
        }).collect(Collectors.toList());
    }

    private BigDecimal calcularCustoTotal(List<ReceitaInsumo> itens) {
        BigDecimal total = itens.stream()
                .map(item -> {
                    Insumo insumo = item.getInsumo();
                    BigDecimal precoPorUnidade = insumo.getPrecoUnidadeCompra()
                            .divide(insumo.getConteudoUnidadeCompra(), 10, RoundingMode.HALF_UP);
                    return precoPorUnidade.multiply(item.getQuantidadeUtilizada());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private ReceitaResponseDTO converterParaResponseDTO(Receita receita) {
        ReceitaResponseDTO dto = new ReceitaResponseDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setRendimento(receita.getRendimento());
        dto.setUnidadeRendimento(receita.getUnidadeRendimento());
        dto.setCustoTotal(receita.getCustoTotal());
        dto.setModoPreparo(receita.getModoPreparo());

        if (receita.getRendimento() != null && receita.getRendimento().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal custoPorUnidade = receita.getCustoTotal()
                    .divide(receita.getRendimento(), 2, RoundingMode.HALF_UP);
            dto.setCustoPorUnidade(custoPorUnidade);
        }

        List<ReceitaInsumoResponseDTO> insumosDTO = receita.getInsumos().stream()
                .map(this::converterInsumoParaDTO)
                .collect(Collectors.toList());
        dto.setInsumos(insumosDTO);

        return dto;
    }

    private ReceitaInsumoResponseDTO converterInsumoParaDTO(ReceitaInsumo item) {
        Insumo insumo = item.getInsumo();

        BigDecimal precoPorUnidade = insumo.getPrecoUnidadeCompra()
                .divide(insumo.getConteudoUnidadeCompra(), 10, RoundingMode.HALF_UP);
        BigDecimal custoInsumo = precoPorUnidade.multiply(item.getQuantidadeUtilizada())
                .setScale(2, RoundingMode.HALF_UP);

        ReceitaInsumoResponseDTO dto = new ReceitaInsumoResponseDTO();
        dto.setInsumoId(insumo.getId());
        dto.setNomeInsumo(insumo.getNome());
        dto.setQuantidadeUtilizada(item.getQuantidadeUtilizada());
        dto.setUnidadeConsumo(insumo.getUnidadeConsumo());
        dto.setCustoInsumo(custoInsumo);
        return dto;
    }
}