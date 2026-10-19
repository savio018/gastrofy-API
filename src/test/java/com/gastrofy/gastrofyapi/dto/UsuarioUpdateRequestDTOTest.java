package com.gastrofy.gastrofyapi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioUpdateRequestDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private UsuarioUpdateRequestDTO dto(String nome, String novaSenha) {
        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO();
        dto.setNome(nome);
        dto.setSenhaAtual("SenhaAtual1");
        dto.setNovaSenha(novaSenha);
        return dto;
    }

    private Set<ConstraintViolation<UsuarioUpdateRequestDTO>> validar(UsuarioUpdateRequestDTO dto) {
        return validator.validate(dto);
    }

    @Test
    void nomeComMenosDe4Caracteres_eInvalido() {
        var violacoes = validar(dto("Ana", null));

        assertThat(violacoes).hasSize(1);
        assertThat(violacoes.iterator().next().getPropertyPath().toString()).isEqualTo("nome");
    }

    @Test
    void novaSenhaCurta_eInvalida() {
        var violacoes = validar(dto("Nome Valido", "Ab1"));

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A senha deve ter pelo menos 8 caracteres");
        assertThat(violacoes)
                .allMatch(v -> v.getPropertyPath().toString().equals("novaSenha"));
    }

    @Test
    void novaSenhaSemMaiuscula_eInvalida() {
        var violacoes = validar(dto("Nome Valido", "semmaiuscula1"));

        assertThat(violacoes).hasSize(1);
        var violacao = violacoes.iterator().next();
        assertThat(violacao.getPropertyPath().toString()).isEqualTo("novaSenha");
        assertThat(violacao.getMessage())
                .isEqualTo("A senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número");
    }

    @Test
    void novaSenhaNula_eValida() {
        assertThat(validar(dto("Nome Valido", null))).isEmpty();
    }
}
