package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.InsumoRequestDTO;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service

public class InsumoService {

private final InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    public Insumo criar(InsumoRequestDTO dto, Usuario usuario){
        Insumo insumo = new Insumo();
        insumo.setNome(dto.getNome());
        insumo.setUnidadeCompra(dto.getUnidadeCompra());
        insumo.setUnidadeConsumo(dto.getUnidadeConsumo());
        insumo.setConteudoUnidadeCompra(dto.getConteudoUnidadeCompra());
        insumo.setPrecoUnidadeCompra(dto.getPrecoUnidadeCompra());
        insumo.setDataValidade(dto.getDataValidade());

        BigDecimal estoqueCalculado = dto.getQuantidadeCompra()
                .multiply(dto.getConteudoUnidadeCompra());

        insumo.setEstoqueTotal(estoqueCalculado);
        insumo.setUsuario(usuario);
        return insumoRepository.save(insumo);
    }

    public List<Insumo> listar(Usuario usuario){
        return insumoRepository.findByUsuarioId(usuario.getIdUsuario());
    }

    public Insumo buscarPorId(Long id, Usuario usuario){

        return insumoRepository.findByIdAndUsuarioId(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Insumo não encontrado"));
    }

    public void deletar(Integer id, Usuario usuario){
        Insumo insumo = buscarPorId(id.longValue(), usuario);
        insumoRepository.delete(insumo);
    }

}
