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
import com.bancodigital.app.dto.ExtratoResponse;
import com.bancodigital.app.exception.ContaNaoEncontradaException;
import com.bancodigital.app.repository.ContaRepository;

/**
 * Testes de unidade do {@link ExtratoService} contra banco H2 em mem\u00f3ria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ExtratoServiceTest {

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    private long criar(String titular, BigDecimal saldo) {
        ContaResponse resposta = contaService.criar(new ContaRequest(titular, saldo));
        return resposta.getId();
    }

    @Test
    void shouldListExtrato_whenAccountExists() {
        long conta = criar("Yara Lopes", new BigDecimal("500.00"));
        contaService.depositar(conta, new BigDecimal("100.00"));
        contaService.sacar(conta, new BigDecimal("50.00"));

        List<ExtratoResponse> extrato = extratoService.listarPorConta(conta);

        assertThat(extrato).hasSize(3);
        assertThat(extrato.get(0).getTipo()).isEqualTo("DEBITO");
        assertThat(extrato.get(0).getDescricao()).isEqualTo("Saque");
        assertThat(extrato.get(1).getTipo()).isEqualTo("CREDITO");
        assertThat(extrato.get(1).getDescricao()).isEqualTo("Dep\u00f3sito");
        assertThat(extrato.get(2).getTipo()).isEqualTo("CREDITO");
        assertThat(extrato.get(2).getDescricao()).isEqualTo("Abertura de conta");
    }

    @Test
    void shouldReturnEmptyExtrato_whenAccountHasNoMovements() {
        long conta = criar("Zeca Silva", BigDecimal.ZERO);

        List<ExtratoResponse> extrato = extratoService.listarPorConta(conta);

        assertThat(extrato).isEmpty(); // abertura com saldo zero nao gera lancamento
    }

    @Test
    void shouldThrow_whenListExtratoOfNonExistingAccount() {
        assertThatThrownBy(() -> extratoService.listarPorConta(999L))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }
}
