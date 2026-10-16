package com.bancodigital.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bancodigital.app.model.Conta;

/**
 * Testes de reposit\u00f3rio de {@link Conta} com @DataJpaTest (H2 em mem\u00f3ria).
 */
@DataJpaTest
class ContaRepositoryTest {

    @Autowired
    private ContaRepository contaRepository;

    @Test
    void shouldSaveAndFindConta() {
        Conta conta = Conta.builder().titular("Maria").saldo(new BigDecimal("10.00")).build();
        Conta salva = contaRepository.save(conta);

        assertThat(salva.getId()).isNotNull();
        Conta encontrada = contaRepository.findById(salva.getId()).orElseThrow();
        assertThat(encontrada.getTitular()).isEqualTo("Maria");
        assertThat(encontrada.getSaldo()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void shouldReturnFalse_whenContaDoesNotExist() {
        assertThat(contaRepository.existsById(999L)).isFalse();
    }
}
