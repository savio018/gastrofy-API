package com.gastrofy.gastrofyapi.exception;

import com.gastrofy.gastrofyapi.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> recursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> regraNegocio(
            RegraNegocioException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacaoBean(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ErrorResponse.CampoErro> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapearCampoErro)
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro de validação nos campos enviados.",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 🔥 ESSE AQUI É O QUE FALTAVA (corrige o teste 15)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "JSON inválido ou formato de data inválido. Use o padrão yyyy-MM-dd para datas.",
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> erroInterno(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno no servidor.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ErrorResponse.CampoErro mapearCampoErro(FieldError fieldError) {
        return new ErrorResponse.CampoErro(
                fieldError.getField(),
                fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Valor inválido"
        );
    }
}