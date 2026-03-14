package com.gastrofy.gastrofyapi.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Object id) {
        super(String.format("%s não encontrado(a) com id: %s", recurso, id));
    }
}
