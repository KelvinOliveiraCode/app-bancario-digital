package com.bancodigital.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para cria\u00e7\u00e3o de conta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaRequest {

    @NotBlank(message = "O titular \u00e9 obrigat\u00f3rio")
    private String titular;

    @NotNull(message = "O saldo inicial \u00e9 obrigat\u00f3rio")
    @DecimalMin(value = "0.0", inclusive = true, message = "O saldo inicial n\u00e3o pode ser negativo")
    private BigDecimal saldoInicial;
}
