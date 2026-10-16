package com.bancodigital.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de sa\u00edda que representa uma transfer\u00eancia conclu\u00edda.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaResponse {

    private Long id;
    private Long contaOrigemId;
    private Long contaDestinoId;
    private BigDecimal valor;
    private LocalDateTime dataTransferencia;
    private String status;
}
