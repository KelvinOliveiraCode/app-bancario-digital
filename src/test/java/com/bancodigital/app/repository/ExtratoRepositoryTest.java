package com.bancodigital.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bancodigital.app.model.Extrato;

/**
 * Testes de reposit\u00f3rio de {@link Extrato} com @DataJpaTest (H2 em mem\u00f3ria).
 */
@DataJpaTest
class ExtratoRepositoryTest {

    @Autowired
    private ExtratoRepository extratoRepository;

    @Test
    void shouldFindByContaIdOrderByDataMovimentoDesc() {
        Extrato a = Extrato.builder()
                .contaId(1L).tipo(Extrato.TipoExtrato.CREDITO)
                .valor(new BigDecimal("10.00")).descricao("A").dataMovimento(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();
        Extrato b = Extrato.builder()
                .contaId(1L).tipo(Extrato.TipoExtrato.DEBITO)
                .valor(new BigDecimal("5.00")).descricao("B").dataMovimento(LocalDateTime.of(2026, 1, 2, 10, 0))
                .build();
        Extrato outraConta = Extrato.builder()
                .contaId(2L).tipo(Extrato.TipoExtrato.CREDITO)
                .valor(new BigDecimal("1.00")).descricao("C").dataMovimento(LocalDateTime.now())
                .build();
        extratoRepository.save(a);
        extratoRepository.save(b);
        extratoRepository.save(outraConta);

        List<Extrato> lancamentos = extratoRepository.findByContaIdOrderByDataMovimentoDesc(1L);

        assertThat(lancamentos).hasSize(2);
        assertThat(lancamentos.get(0).getDescricao()).isEqualTo("B");
        assertThat(lancamentos.get(1).getDescricao()).isEqualTo("A");
    }
}
