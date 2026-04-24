package com.notasfiscais.exception;

/**
 * Exceções de domínio usadas nos controllers.
 * Seguindo o padrão do modelo: tratamento inline em cada controller.
 */
public class GlobalExceptionHandler {

    private GlobalExceptionHandler() {}

    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String message) {
            super(message);
        }
    }

    public static class AcessoNegadoException extends RuntimeException {
        public AcessoNegadoException(String message) {
            super(message);
        }
    }

    public static class ValidacaoException extends RuntimeException {
        public ValidacaoException(String message) {
            super(message);
        }
    }
}
