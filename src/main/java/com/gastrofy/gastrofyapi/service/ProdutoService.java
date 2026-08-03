package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.ProdutoRequestDTO;
import com.gastrofy.gastrofyapi.dto.ProdutoResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.Produto;
import com.gastrofy.gastrofyapi.model.Receita;
import com.gastrofy.gastrofyapi.model.ReceitaInsumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import com.gastrofy.gastrofyapi.repository.ProdutoRepository;
import com.gastrofy.gastrofyapi.repository.ReceitaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ReceitaRepository receitaRepository;
    private final InsumoRepository insumoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ReceitaRepository receitaRepository, InsumoRepository insumoRepository) {
        this.produtoRepository = produtoRepository;
        this.receitaRepository = receitaRepository;
        this.insumoRepository = insumoRepository;
    }

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

        calcularCustoELucro(produto, receita);

        Produto salvo = produtoRepository.save(produto);
        return converterParaResponseDTO(salvo);
    }

    public List<ProdutoResponseDTO> listar(Usuario usuario) {
        return produtoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        return converterParaResponseDTO(produto);
    }

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

        calcularCustoELucro(produto, receita);

        Produto atualizado = produtoRepository.save(produto);
        return converterParaResponseDTO(atualizado);
    }

    public void deletar(Long id, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        produtoRepository.delete(produto);
    }

    public ProdutoResponseDTO produzir(Long id, BigDecimal quantidade, Usuario usuario) {
        Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        Receita receita = produto.getReceita();
        BigDecimal totalReceita = produto.getQuantidadeReceita().multiply(quantidade);

        for (ReceitaInsumo item : receita.getInsumos()) {
            Insumo insumo = item.getInsumo();
            BigDecimal quantidadeUsada = item.getQuantidadeUtilizada().multiply(totalReceita);
            BigDecimal novoEstoque = insumo.getEstoqueTotal().subtract(quantidadeUsada);

            if (novoEstoque.compareTo(BigDecimal.ZERO) < 0) {
                throw new RegraNegocioException("Estoque insuficiente para o insumo: " + insumo.getNome());
            }

            insumo.setEstoqueTotal(novoEstoque);
            insumoRepository.save(insumo);
        }

        return converterParaResponseDTO(produto);
    }

    private void calcularCustoELucro(Produto produto, Receita receita) {
        BigDecimal custoTotal = receita.getCustoTotal()
                .multiply(produto.getQuantidadeReceita())
                .setScale(2, RoundingMode.HALF_UP);

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
        return dto;
    }
}