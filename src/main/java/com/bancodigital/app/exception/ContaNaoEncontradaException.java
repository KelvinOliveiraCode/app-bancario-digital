package com.bancodigital.app.exception;

import lombok.Getter;

/**
 * Exce\u00e7\u00e3o lançada quando uma conta n\u00e3o \u00e9 encontrada no reposit\u00f3rio.
 */
@Getter
public class ContaNaoEncontradaException extends RuntimeException {

    private final Long contaId;

    public ContaNaoEncontradaException(Long contaId) {
        super("Conta n\u00e3o encontrada: id=" + contaId);
        this.contaId = contaId;
    }
}
