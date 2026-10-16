package com.bancodigital.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de sa\u00edda que representa uma conta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaResponse {

    private Long id;
    private String titular;
    private BigDecimal saldo;
    private LocalDateTime criadoEm;
}
