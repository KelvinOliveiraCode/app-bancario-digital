package com.bancodigital.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bancodigital.app.dto.ContaRequest;
import com.bancodigital.app.dto.ContaResponse;
import com.bancodigital.app.dto.TransferenciaRequest;
import com.bancodigital.app.exception.ContaNaoEncontradaException;
import com.bancodigital.app.exception.OperacaoInvalidaException;
import com.bancodigital.app.exception.SaldoInsuficienteException;
import com.bancodigital.app.repository.ContaRepository;
import com.bancodigital.app.repository.ExtratoRepository;

/**
 * Testes de unidade do {@link TransferenciaService} contra banco H2 em mem\u00f3ria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TransferenciaServiceTest {

    @Autowired
    private TransferenciaService transferenciaService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ExtratoRepository extratoRepository;

    private long criar(String titular, BigDecimal saldo) {
        ContaResponse resposta = contaService.criar(new ContaRequest(titular, saldo));
        return resposta.getId();
    }

    private BigDecimal saldoDe(long contaId) {
        return contaRepository.findById(contaId)
                .map(com.bancodigital.app.model.Conta::getSaldo)
                .orElseThrow(() -> new java.util.NoSuchElementException("conta " + contaId));
    }

    private long lancamentosDe(long contaId) {
        return extratoRepository.findByContaIdOrderByDataMovimentoDesc(contaId).size();
    }

    @Test
    void shouldTransfer_whenSufficientBalance() {
        long origem = criar("Lucas Pereira", new BigDecimal("1000.00"));
        long destino = criar("Mariana Torres", new BigDecimal("100.00"));

        var resposta = transferenciaService.transferir(new TransferenciaRequest(
                origem, destino, new BigDecimal("300.00")));

        assertThat(resposta.getId()).isNotNull();
        assertThat(resposta.getContaOrigemId()).isEqualTo(origem);
        assertThat(resposta.getContaDestinoId()).isEqualTo(destino);
        assertThat(resposta.getValor()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(resposta.getStatus()).isEqualTo("CONCLUIDA");

        assertThat(saldoDe(origem)).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(saldoDe(destino)).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    void shouldThrow_whenInsufficientBalance() {
        long origem = criar("Nina Rocha", new BigDecimal("50.00"));
        long destino = criar("Oscar Freitas", new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferenciaService.transferir(new TransferenciaRequest(
                origem, destino, new BigDecimal("200.00"))))
                .isInstanceOf(SaldoInsuficienteException.class);

        assertThat(saldoDe(origem)).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(saldoDe(destino)).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void shouldThrow_whenSameAccountOriginAndDestination() {
        long conta = criar("Paula Mendes", new BigDecimal("500.00"));

        assertThatThrownBy(() -> transferenciaService.transferir(new TransferenciaRequest(
                conta, conta, new BigDecimal("100.00"))))
                .isInstanceOf(OperacaoInvalidaException.class);
    }

    @Test
    void shouldThrow_whenOriginAccountNotFound() {
        long destino = criar("Quenia Dias", new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferenciaService.transferir(new TransferenciaRequest(
                999L, destino, new BigDecimal("10.00"))))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void shouldThrow_whenDestinationAccountNotFound() {
        long origem = criar("Ricardo Lima", new BigDecimal("100.00"));

        assertThatThrownBy(() -> transferenciaService.transferir(new TransferenciaRequest(
                origem, 999L, new BigDecimal("10.00"))))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void shouldThrow_whenNonPositiveValue() {
        long origem = criar("Sofia Alves", new BigDecimal("100.00"));
        long destino = criar("Tiago Gomes", new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferenciaService.transferir(new TransferenciaRequest(
                origem, destino, BigDecimal.ZERO)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> transferenciaService.transferir(new TransferenciaRequest(
                origem, destino, new BigDecimal("-10.00"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateExtratoEntries_whenTransferSucceeds() {
        long origem = criar("Ursula Castro", new BigDecimal("500.00"));
        long destino = criar("Vinicius Melo", new BigDecimal("200.00"));

        transferenciaService.transferir(new TransferenciaRequest(
                origem, destino, new BigDecimal("150.00")));

        assertThat(lancamentosDe(origem)).isGreaterThan(0);
        assertThat(lancamentosDe(destino)).isGreaterThan(0);
    }

    @Test
    void shouldListAllContas_whenSomeCreated() {
        contaRepository.deleteAll();
        criar("Walter Souza", new BigDecimal("10.00"));
        criar("Xuxa Nunes", new BigDecimal("20.00"));

        assertThat(contaService.listarTodas()).hasSize(2);
    }
}
