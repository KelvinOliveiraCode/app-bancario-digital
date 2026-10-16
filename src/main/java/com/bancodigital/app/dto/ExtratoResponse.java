package com.bancodigital.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de sa\u00edda que representa um lan\u00e7amento do extrato.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtratoResponse {

    private Long id;
    private Long contaId;
    private String tipo;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime dataMovimento;
}
