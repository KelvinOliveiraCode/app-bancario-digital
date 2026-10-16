package com.bancodigital.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bancodigital.app.model.Transferencia;

/**
 * Testes de reposit\u00f3rio de {@link Transferencia} com @DataJpaTest (H2 em mem\u00f3ria).
 */
@DataJpaTest
class TransferenciaRepositoryTest {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Test
    void shouldSaveAndFindTransferencia() {
        Transferencia t = Transferencia.builder()
                .contaOrigemId(1L)
                .contaDestinoId(2L)
                .valor(new BigDecimal("50.00"))
                .dataTransferencia(LocalDateTime.now())
                .build();
        Transferencia salva = transferenciaRepository.save(t);

        assertThat(salva.getId()).isNotNull();
        Transferencia encontrada = transferenciaRepository.findById(salva.getId()).orElseThrow();
        assertThat(encontrada.getValor()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(encontrada.getStatus()).isEqualTo(Transferencia.StatusTransferencia.CONCLUIDA);
    }
}
