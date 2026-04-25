package com.notasfiscais.application.exceptions;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
