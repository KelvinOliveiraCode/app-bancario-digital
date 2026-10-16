package com.bancodigital.app.exception;

import lombok.Getter;

/**
 * Exce\u00e7\u00e3o lançada quando o saldo da conta n\u00e3o \u00e9 suficiente para uma
 * opera\u00e7\u00e3o.
 */
@Getter
public class SaldoInsuficienteException extends RuntimeException {

    private final Long contaId;
    private final java.math.BigDecimal saldo;
    private final java.math.BigDecimal valorSolicitado;

    public SaldoInsuficienteException(Long contaId,
                                      java.math.BigDecimal saldo,
                                      java.math.BigDecimal valorSolicitado) {
        super("Saldo insuficiente na conta " + contaId
              + ": saldo=" + saldo + ", solicitado=" + valorSolicitado);
        this.contaId = contaId;
        this.saldo = saldo;
        this.valorSolicitado = valorSolicitado;
    }
}
