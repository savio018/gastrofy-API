package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.ProdutoEmbalagemRequestDTO;
import com.gastrofy.gastrofyapi.dto.ProdutoEmbalagemResponseDTO;
import com.gastrofy.gastrofyapi.dto.ProdutoRequestDTO;
import com.gastrofy.gastrofyapi.dto.ProdutoResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.*;
import com.gastrofy.gastrofyapi.repository.EmbalagemRepository;
import com.gastrofy.gastrofyapi.repository.ProdutoRepository;
import com.gastrofy.gastrofyapi.repository.ReceitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ReceitaRepository receitaRepository;
    private final InsumoService insumoService;
    private final EmbalagemRepository embalagemRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ReceitaRepository receitaRepository,
                          InsumoService insumoService, EmbalagemRepository embalagemRepository) {
        this.produtoRepository = produtoRepository;
        this.receitaRepository = receitaRepository;
        this.insumoService = insumoService;
        this.embalagemRepository = embalagemRepository;
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto, Usuario usuario) {
        Receita receita = receitaRepository.findByIdAndUsuarioIdUsuario(dto.getReceitaId(), usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", dto.getReceitaId()));

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setReceita(receita);
        produto.setQuantidadeReceita(dto.getQuantidadeReceita());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setUsuario(usuario);
        produto.setEstoqueProduto(BigDecimal.ZERO);

        List<ProdutoEmbalagem> embalagens = montarEmbalagens(dto.getEmbalagens(), produto, usuario);
        produto.setEmbalagens(embalagens);

        calcularCustoELucro(produto, receita);

        Produto salvo = produtoRepository.save(produto);
        return converterParaResponseDTO(salvo);
    }

    @Transactional
    public List<ProdutoResponseDTO> listar(Usuario usuario) {
        return produtoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoResponseDTO buscarPorId(Long id, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        return converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        Receita receita = receitaRepository.findByIdAndUsuarioIdUsuario(dto.getReceitaId(), usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", dto.getReceitaId()));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setReceita(receita);
        produto.setQuantidadeReceita(dto.getQuantidadeReceita());
        produto.setPrecoVenda(dto.getPrecoVenda());

        produto.getEmbalagens().clear();
        List<ProdutoEmbalagem> novasEmbalagens = montarEmbalagens(dto.getEmbalagens(), produto, usuario);
        produto.getEmbalagens().addAll(novasEmbalagens);

        calcularCustoELucro(produto, receita);

        Produto atualizado = produtoRepository.save(produto);
        return converterParaResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        produtoRepository.delete(produto);
    }

    @Transactional
    public ProdutoResponseDTO produzir(Long id, BigDecimal quantidade, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("A quantidade a produzir deve ser maior que zero");
        }

        Receita receita = produto.getReceita();
        BigDecimal totalReceita = produto.getQuantidadeReceita().multiply(quantidade);

        // Desconta insumos da receita seguindo FEFO (lote que vence primeiro é usado primeiro)
        for (ReceitaInsumo item : receita.getInsumos()) {
            Insumo insumo = item.getInsumo();
            BigDecimal quantidadeUsada = item.getQuantidadeUtilizada().multiply(totalReceita);
            insumoService.descontarEstoque(insumo, quantidadeUsada);
        }

        // Valida se há estoque suficiente de TODAS as embalagens ANTES de descontar qualquer uma
        int quantidadeInteira = quantidade.intValue();
        for (ProdutoEmbalagem item : produto.getEmbalagens()) {
            Embalagem embalagem = item.getEmbalagem();
            int necessario = item.getQuantidadeUtilizada() * quantidadeInteira;

            if (embalagem.getEstoqueTotal() < necessario) {
                throw new RegraNegocioException(
                        "Estoque insuficiente da embalagem '" + embalagem.getNome() + "'. " +
                                "Disponível: " + embalagem.getEstoqueTotal() + ", necessário: " + necessario + "."
                );
            }
        }

        // Só desconta as embalagens depois que TODAS passaram na validação
        for (ProdutoEmbalagem item : produto.getEmbalagens()) {
            Embalagem embalagem = item.getEmbalagem();
            int necessario = item.getQuantidadeUtilizada() * quantidadeInteira;
            embalagem.setEstoqueTotal(embalagem.getEstoqueTotal() - necessario);
            embalagemRepository.save(embalagem);
        }

        BigDecimal estoqueAtual = produto.getEstoqueProduto() != null ? produto.getEstoqueProduto() : BigDecimal.ZERO;
        produto.setEstoqueProduto(estoqueAtual.add(quantidade));

        Produto atualizado = produtoRepository.save(produto);
        return converterParaResponseDTO(atualizado);
    }

    private List<ProdutoEmbalagem> montarEmbalagens(List<ProdutoEmbalagemRequestDTO> dtos, Produto produto, Usuario usuario) {
        if (dtos == null) return new ArrayList<>();

        return dtos.stream().map(dto -> {
            Embalagem embalagem = embalagemRepository.findByIdAndUsuarioIdUsuario(dto.getEmbalagemId(), usuario.getIdUsuario())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Embalagem", dto.getEmbalagemId()));

            ProdutoEmbalagem item = new ProdutoEmbalagem();
            item.setProduto(produto);
            item.setEmbalagem(embalagem);
            item.setQuantidadeUtilizada(dto.getQuantidadeUtilizada());
            return item;
        }).collect(Collectors.toList());
    }

    private void calcularCustoELucro(Produto produto, Receita receita) {
        BigDecimal custoReceita = receita.getCustoTotal()
                .multiply(produto.getQuantidadeReceita());

        BigDecimal custoEmbalagens = produto.getEmbalagens().stream()
                .map(item -> item.getEmbalagem().getPrecoUnidadeCompra()
                        .divide(BigDecimal.valueOf(item.getEmbalagem().getConteudoUnidadeCompra()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(item.getQuantidadeUtilizada())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoTotal = custoReceita.add(custoEmbalagens).setScale(2, RoundingMode.HALF_UP);

        BigDecimal lucro = produto.getPrecoVenda()
                .subtract(custoTotal)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal margemLucro = lucro
                .divide(produto.getPrecoVenda(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        produto.setCustoTotal(custoTotal);
        produto.setLucro(lucro);
        produto.setMargemLucro(margemLucro);
    }

    private ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setReceitaId(produto.getReceita().getId());
        dto.setNomeReceita(produto.getReceita().getNome());
        dto.setQuantidadeReceita(produto.getQuantidadeReceita());
        dto.setPrecoVenda(produto.getPrecoVenda());
        dto.setCustoTotal(produto.getCustoTotal());
        dto.setLucro(produto.getLucro());
        dto.setMargemLucro(produto.getMargemLucro());
        dto.setEstoqueProduto(produto.getEstoqueProduto());

        List<ProdutoEmbalagemResponseDTO> embalagensDTO = produto.getEmbalagens().stream()
                .map(item -> {
                    ProdutoEmbalagemResponseDTO e = new ProdutoEmbalagemResponseDTO();
                    e.setEmbalagemId(item.getEmbalagem().getId());
                    e.setNomeEmbalagem(item.getEmbalagem().getNome());
                    e.setQuantidadeUtilizada(item.getQuantidadeUtilizada());
                    return e;
                })
                .collect(Collectors.toList());
        dto.setEmbalagens(embalagensDTO);

        return dto;
    }
}