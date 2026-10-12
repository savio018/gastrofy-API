package com.gastrofy.gastrofyapi.service;

import com.gastrofy.gastrofyapi.dto.AlertaResponseDTO;
import com.gastrofy.gastrofyapi.model.Embalagem;
import com.gastrofy.gastrofyapi.model.Insumo;
import com.gastrofy.gastrofyapi.model.LoteInsumo;
import com.gastrofy.gastrofyapi.model.Usuario;
import com.gastrofy.gastrofyapi.repository.EmbalagemRepository;
import com.gastrofy.gastrofyapi.repository.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaService {

    private static final String CATEGORIA_INSUMO = "INSUMO";
    private static final String CATEGORIA_EMBALAGEM = "EMBALAGEM";
    private static final DateTimeFormatter FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InsumoRepository insumoRepository;
    private final EmbalagemRepository embalagemRepository;

    public AlertaService(InsumoRepository insumoRepository, EmbalagemRepository embalagemRepository) {
        this.insumoRepository = insumoRepository;
        this.embalagemRepository = embalagemRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertaResponseDTO> gerarAlertas(Usuario usuario) {
        List<Insumo> insumos = insumoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        List<Embalagem> embalagens = embalagemRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        List<AlertaResponseDTO> alertas = new ArrayList<>();

        for (Insumo insumo : insumos) {
            alertas.addAll(verificarEstoque(insumo));
            alertas.addAll(verificarValidadeLotes(insumo));
        }

        for (Embalagem embalagem : embalagens) {
            alertas.addAll(verificarEstoqueEmbalagem(embalagem));
        }

        return alertas;
    }

    private List<AlertaResponseDTO> verificarEstoque(Insumo insumo) {
        List<AlertaResponseDTO> alertas = new ArrayList<>();

        BigDecimal estoqueTotal = insumo.getLotes().stream()
                .map(LoteInsumo::getQuantidade)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (estoqueTotal.signum() == 0) {
            alertas.add(alertaInsumo("SEM_ESTOQUE", "Sem estoque disponível!", insumo, null));
            return alertas;
        }

        if (insumo.getEstoqueCritico() != null &&
                estoqueTotal.compareTo(insumo.getEstoqueCritico()) <= 0) {
            alertas.add(alertaInsumo("ESTOQUE_CRITICO",
                    "Estoque crítico! Apenas " + estoqueTotal + " " + insumo.getUnidadeConsumo() + " restantes.",
                    insumo, null));
            return alertas;
        }

        if (insumo.getEstoqueMinimo() != null &&
                estoqueTotal.compareTo(insumo.getEstoqueMinimo()) <= 0) {
            alertas.add(alertaInsumo("ESTOQUE_BAIXO",
                    "Estoque baixo! Apenas " + estoqueTotal + " " + insumo.getUnidadeConsumo() + " restantes.",
                    insumo, null));
        }

        return alertas;
    }

    private List<AlertaResponseDTO> verificarValidadeLotes(Insumo insumo) {
        List<AlertaResponseDTO> alertas = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        for (LoteInsumo lote : insumo.getLotes()) {
            LocalDate validade = lote.getDataValidade();

            if (validade.isBefore(hoje)) {
                alertas.add(alertaInsumo("VENCIDO",
                        "Lote vencido! Venceu em " + validade.format(FORMATTER_BR) +
                                " (" + lote.getQuantidade() + " " + insumo.getUnidadeConsumo() + ").",
                        insumo, lote.getId()));
                continue;
            }

            if (insumo.getDiasAvisoValidade() != null) {
                LocalDate dataAviso = hoje.plusDays(insumo.getDiasAvisoValidade());

                if (!validade.isAfter(dataAviso)) {
                    long diasRestantes = ChronoUnit.DAYS.between(hoje, validade);
                    alertas.add(alertaInsumo("VALIDADE_PROXIMA",
                            "Lote vence em " + diasRestantes + " dia(s)! Data de validade: " +
                                    validade.format(FORMATTER_BR) + " (" + lote.getQuantidade() + " " +
                                    insumo.getUnidadeConsumo() + ").",
                            insumo, lote.getId()));
                }
            }
        }

        return alertas;
    }

    private List<AlertaResponseDTO> verificarEstoqueEmbalagem(Embalagem embalagem) {
        List<AlertaResponseDTO> alertas = new ArrayList<>();

        if (embalagem.getEstoqueTotal() == null) return alertas;

        if (embalagem.getEstoqueTotal() == 0) {
            alertas.add(alertaEmbalagem("SEM_ESTOQUE", "Sem estoque disponível!", embalagem));
            return alertas;
        }

        if (embalagem.getEstoqueCritico() != null &&
                embalagem.getEstoqueTotal() <= embalagem.getEstoqueCritico()) {
            alertas.add(alertaEmbalagem("ESTOQUE_CRITICO",
                    "Estoque crítico! Apenas " + embalagem.getEstoqueTotal() + " unidades restantes.",
                    embalagem));
            return alertas;
        }

        if (embalagem.getEstoqueMinimo() != null &&
                embalagem.getEstoqueTotal() <= embalagem.getEstoqueMinimo()) {
            alertas.add(alertaEmbalagem("ESTOQUE_BAIXO",
                    "Estoque baixo! Apenas " + embalagem.getEstoqueTotal() + " unidades restantes.",
                    embalagem));
        }

        return alertas;
    }

    private AlertaResponseDTO alertaInsumo(String tipo, String mensagem, Insumo insumo, Long loteId) {
        return new AlertaResponseDTO(tipo, mensagem, insumo.getNome(), insumo.getId(), loteId, CATEGORIA_INSUMO);
    }

    private AlertaResponseDTO alertaEmbalagem(String tipo, String mensagem, Embalagem embalagem) {
        return new AlertaResponseDTO(tipo, mensagem, embalagem.getNome(), embalagem.getId(), null, CATEGORIA_EMBALAGEM);
    }
}