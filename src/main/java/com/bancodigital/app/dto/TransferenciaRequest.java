package com.bancodigital.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para solicita\u00e7\u00e3o de transfer\u00eancia.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaRequest {

    @NotNull(message = "A conta de origem \u00e9 obrigat\u00f3ria")
    private Long contaOrigemId;

    @NotNull(message = "A conta de destino \u00e9 obrigat\u00f3ria")
    private Long contaDestinoId;

    @NotNull(message = "O valor \u00e9 obrigat\u00f3rio")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero")
    private BigDecimal valor;
}
