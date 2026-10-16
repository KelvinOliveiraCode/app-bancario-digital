package com.bancodigital.app.exception;

/**
 * Exce\u00e7\u00e3o lançada para regras de neg\u00f3cio violadas (ex.: mesma conta de
 * origem e destino, valor inv\u00e1lido).
 */
public class OperacaoInvalidaException extends RuntimeException {

    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
