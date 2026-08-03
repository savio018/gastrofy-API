package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.AlertaResponseDTO;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaService {

    private final InsumoRepository insumoRepository;

    public AlertaService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    public List<AlertaResponseDTO> gerarAlertas(Usuario usuario) {
        List<Insumo> insumos = insumoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        List<AlertaResponseDTO> alertas = new ArrayList<>();

        for (Insumo insumo : insumos) {
            alertas.addAll(verificarEstoque(insumo));
            alertas.addAll(verificarValidade(insumo));
        }

        return alertas;
    }

    private List<AlertaResponseDTO> verificarEstoque(Insumo insumo) {
        List<AlertaResponseDTO> alertas = new ArrayList<>();

        if (insumo.getEstoqueTotal() == null) return alertas;

        // Sem estoque
        if (insumo.getEstoqueTotal().signum() == 0) {
            alertas.add(new AlertaResponseDTO(
                    "SEM_ESTOQUE",
                    "Sem estoque disponível!",
                    insumo.getNome()
            ));
            return alertas;
        }

        // Estoque crítico
        if (insumo.getEstoqueCritico() != null &&
                insumo.getEstoqueTotal().compareTo(insumo.getEstoqueCritico()) <= 0) {
            alertas.add(new AlertaResponseDTO(
                    "ESTOQUE_CRITICO",
                    "Estoque crítico! Apenas " + insumo.getEstoqueTotal() + " " + insumo.getUnidadeConsumo() + " restantes.",
                    insumo.getNome()
            ));
            return alertas;
        }

        // Estoque mínimo
        if (insumo.getEstoqueMinimo() != null &&
                insumo.getEstoqueTotal().compareTo(insumo.getEstoqueMinimo()) <= 0) {
            alertas.add(new AlertaResponseDTO(
                    "ESTOQUE_BAIXO",
                    "Estoque baixo! Apenas " + insumo.getEstoqueTotal() + " " + insumo.getUnidadeConsumo() + " restantes.",
                    insumo.getNome()
            ));
        }

        return alertas;
    }

    private List<AlertaResponseDTO> verificarValidade(Insumo insumo) {
        List<AlertaResponseDTO> alertas = new ArrayList<>();

        if (insumo.getDataValidade() == null) return alertas;

        LocalDate hoje = LocalDate.now();
        LocalDate validade = insumo.getDataValidade();

        // Produto vencido
        if (validade.isBefore(hoje)) {
            alertas.add(new AlertaResponseDTO(
                    "VENCIDO",
                    "Produto vencido! Venceu em " + validade + ".",
                    insumo.getNome()
            ));
            return alertas;
        }

        // Validade próxima
        if (insumo.getDiasAvisoValidade() != null) {
            LocalDate dataAviso = hoje.plusDays(insumo.getDiasAvisoValidade());

            if (!validade.isAfter(dataAviso)) {
                long diasRestantes = hoje.until(validade).getDays();
                alertas.add(new AlertaResponseDTO(
                        "VALIDADE_PROXIMA",
                        "Vence em " + diasRestantes + " dia(s)! Data de validade: " + validade + ".",
                        insumo.getNome()
                ));
            }
        }

        return alertas;
    }
}
