package com.bancodigital.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lan\u00e7amento do extrato de uma conta (cr\u00e9dito ou d\u00e9bito).
 */
@Entity
@Table(name = "extratos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Extrato {

    public enum TipoExtrato {
        CREDITO, DEBITO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoExtrato tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(name = "data_movimento", nullable = false)
    @Builder.Default
    private LocalDateTime dataMovimento = LocalDateTime.now();
}
