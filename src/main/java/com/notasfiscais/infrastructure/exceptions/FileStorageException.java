package com.notasfiscais.infrastructure.exceptions;
public class FileStorageException extends RuntimeException {
    public FileStorageException(String mensagem) { super(mensagem); }
}
