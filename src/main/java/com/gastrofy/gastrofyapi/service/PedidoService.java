package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.PedidoItemRequestDTO;
import com.gastrofy.gastrofyapi.dto.PedidoItemResponseDTO;
import com.gastrofy.gastrofyapi.dto.PedidoRequestDTO;
import com.gastrofy.gastrofyapi.dto.PedidoResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.exception.RegraNegocioException;
import com.gastrofy.gastrofyapi.model.*;
import com.gastrofy.gastrofyapi.repository.ClienteRepository;
import com.gastrofy.gastrofyapi.repository.PedidoRepository;
import com.gastrofy.gastrofyapi.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    public PedidoResponseDTO criar(PedidoRequestDTO dto, Usuario usuario) {
        Cliente cliente = clienteRepository.findByIdAndUsuarioIdUsuario(dto.getClienteId(), usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", dto.getClienteId()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setNomeCliente(cliente.getNome());
        pedido.setTelefone(cliente.getTelefone());
        pedido.setObservacao(dto.getObservacao());
        pedido.setDataEntrega(dto.getDataEntrega());
        pedido.setUsuario(usuario);

        List<PedidoItem> itens = montarItens(dto.getItens(), pedido, usuario);
        pedido.setItens(itens);

        calcularTotais(pedido);

        Pedido salvo = pedidoRepository.save(pedido);
        return converterParaResponseDTO(salvo);
    }

    public List<PedidoResponseDTO> listar(Usuario usuario) {
        return pedidoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPorId(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
        return converterParaResponseDTO(pedido);
    }

    public PedidoResponseDTO produzir(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new RegraNegocioException("Apenas pedidos com status PENDENTE podem ser produzidos");
        }

        pedido.setStatus(StatusPedido.PRODUZIDO);
        Pedido atualizado = pedidoRepository.save(pedido);
        return converterParaResponseDTO(atualizado);
    }

    public PedidoResponseDTO cancelar(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));

        if (pedido.getStatus() == StatusPedido.PRODUZIDO) {
            throw new RegraNegocioException("Pedidos já produzidos não podem ser cancelados");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido atualizado = pedidoRepository.save(pedido);
        return converterParaResponseDTO(atualizado);
    }

    public void deletar(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));

        if (pedido.getStatus() == StatusPedido.PRODUZIDO) {
            throw new RegraNegocioException("Pedidos já produzidos não podem ser deletados");
        }

        pedidoRepository.delete(pedido);
    }

    private List<PedidoItem> montarItens(List<PedidoItemRequestDTO> dtos, Pedido pedido, Usuario usuario) {
        return dtos.stream().map(dto -> {
            Produto produto = produtoRepository.findByIdAndUsuarioIdUsuario(dto.getProdutoId(), usuario.getIdUsuario())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", dto.getProdutoId()));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(dto.getQuantidade());
            item.setPrecoUnitario(produto.getPrecoVenda());
            item.setCustoUnitario(produto.getCustoTotal());
            item.setSubtotalVenda(produto.getPrecoVenda().multiply(dto.getQuantidade()).setScale(2, RoundingMode.HALF_UP));
            item.setSubtotalCusto(produto.getCustoTotal().multiply(dto.getQuantidade()).setScale(2, RoundingMode.HALF_UP));
            return item;
        }).collect(Collectors.toList());
    }

    private void calcularTotais(Pedido pedido) {
        BigDecimal totalVenda = pedido.getItens().stream()
                .map(PedidoItem::getSubtotalVenda)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCusto = pedido.getItens().stream()
                .map(PedidoItem::getSubtotalCusto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLucro = totalVenda.subtract(totalCusto).setScale(2, RoundingMode.HALF_UP);

        pedido.setTotalVenda(totalVenda.setScale(2, RoundingMode.HALF_UP));
        pedido.setTotalCusto(totalCusto.setScale(2, RoundingMode.HALF_UP));
        pedido.setTotalLucro(totalLucro);
    }

    private PedidoResponseDTO converterParaResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setClienteId(pedido.getCliente().getId());
        dto.setNomeCliente(pedido.getNomeCliente());
        dto.setTelefone(pedido.getTelefone());
        dto.setObservacao(pedido.getObservacao());
        dto.setStatus(pedido.getStatus());
        dto.setTotalVenda(pedido.getTotalVenda());
        dto.setTotalCusto(pedido.getTotalCusto());
        dto.setTotalLucro(pedido.getTotalLucro());
        dto.setDataCriacao(pedido.getDataCriacao());
        dto.setDataEntrega(pedido.getDataEntrega());

        List<PedidoItemResponseDTO> itensDTO = pedido.getItens().stream()
                .map(this::converterItemParaDTO)
                .collect(Collectors.toList());
        dto.setItens(itensDTO);

        return dto;
    }

    private PedidoItemResponseDTO converterItemParaDTO(PedidoItem item) {
        PedidoItemResponseDTO dto = new PedidoItemResponseDTO();
        dto.setProdutoId(item.getProduto().getId());
        dto.setNomeProduto(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setCustoUnitario(item.getCustoUnitario());
        dto.setSubtotalVenda(item.getSubtotalVenda());
        dto.setSubtotalCusto(item.getSubtotalCusto());
        return dto;
    }
}