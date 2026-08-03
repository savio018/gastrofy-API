package com.gastrofy.gastrofyapi.service;


import com.gastrofy.gastrofyapi.dto.ClienteRequestDTO;
import com.gastrofy.gastrofyapi.dto.ClienteResponseDTO;
import com.gastrofy.gastrofyapi.exception.RecursoNaoEncontradoException;
import com.gastrofy.gastrofyapi.model.Cliente;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponseDTO criar(ClienteRequestDTO dto, Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setEndereco(dto.getEndereco());
        cliente.setObservacao(dto.getObservacao());
        cliente.setUsuario(usuario);

        Cliente salvo = clienteRepository.save(cliente);
        return converterParaResponseDTO(salvo);
    }

    public List<ClienteResponseDTO> listar(Usuario usuario) {
        return clienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO buscarPorId(Long id, Usuario usuario) {
        Cliente cliente = clienteRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
        return converterParaResponseDTO(cliente);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto, Usuario usuario) {
        Cliente cliente = clienteRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));

        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setEndereco(dto.getEndereco());
        cliente.setObservacao(dto.getObservacao());

        Cliente atualizado = clienteRepository.save(cliente);
        return converterParaResponseDTO(atualizado);
    }

    public void deletar(Long id, Usuario usuario) {
        Cliente cliente = clienteRepository.findByIdAndUsuarioIdUsuario(id, usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
        clienteRepository.delete(cliente);
    }

    private ClienteResponseDTO converterParaResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setEndereco(cliente.getEndereco());
        dto.setObservacao(cliente.getObservacao());
        return dto;
    }
}
