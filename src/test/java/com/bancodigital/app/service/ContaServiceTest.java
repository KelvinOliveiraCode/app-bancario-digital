package com.bancodigital.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bancodigital.app.dto.ContaRequest;
import com.bancodigital.app.dto.ContaResponse;
import com.bancodigital.app.exception.ContaNaoEncontradaException;
import com.bancodigital.app.exception.SaldoInsuficienteException;
import com.bancodigital.app.model.Conta;
import com.bancodigital.app.repository.ContaRepository;

/**
 * Testes de unidade do {@link ContaService} contra banco H2 em mem\u00f3ria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ContaServiceTest {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    private ContaResponse criarConta(String titular, BigDecimal saldoInicial) {
        return contaService.criar(new ContaRequest(titular, saldoInicial));
    }

    @Test
    void shouldCreateAccount_whenValidRequest() {
        ContaResponse resposta = criarConta("Ana Silva", new BigDecimal("100.00"));

        assertThat(resposta.getId()).isNotNull();
        assertThat(resposta.getTitular()).isEqualTo("Ana Silva");
        assertThat(resposta.getSaldo()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(resposta.getCriadoEm()).isNotNull();
        assertThat(contaRepository.existsById(resposta.getId())).isTrue();
    }

    @Test
    void shouldCreateAccount_whenSaldoInicialZero() {
        ContaResponse resposta = criarConta("Bruno Costa", BigDecimal.ZERO);

        assertThat(resposta.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(contaRepository.existsById(resposta.getId())).isTrue();
    }

    @Test
    void shouldDeposit_whenAccountExists() {
        ContaResponse conta = criarConta("Carlos Dias", new BigDecimal("50.00"));

        ContaResponse atualizada = contaService.depositar(conta.getId(),
                new BigDecimal("250.00"));

        assertThat(atualizada.getSaldo()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    void shouldThrow_whenDepositIntoNonExistingAccount() {
        assertThatThrownBy(() -> contaService.depositar(999L, new BigDecimal("10.00")))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void shouldThrow_whenDepositNonPositiveValue() {
        ContaResponse conta = criarConta("Daniela Lima", new BigDecimal("10.00"));

        assertThatThrownBy(() -> contaService.depositar(conta.getId(), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> contaService.depositar(conta.getId(),
                new BigDecimal("-5.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldWithdraw_whenSufficientBalance() {
        ContaResponse conta = criarConta("Eduardo Melo", new BigDecimal("500.00"));

        ContaResponse atualizada = contaService.sacar(conta.getId(), new BigDecimal("200.00"));

        assertThat(atualizada.getSaldo()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    void shouldThrow_whenWithdrawInsufficientBalance() {
        ContaResponse conta = criarConta("Fernanda Alves", new BigDecimal("100.00"));

        assertThatThrownBy(() -> contaService.sacar(conta.getId(), new BigDecimal("200.00")))
                .isInstanceOf(SaldoInsuficienteException.class);
        // saldo deve permanecer inalterado
        ContaResponse ainda = contaService.buscarPorId(conta.getId());
        assertThat(ainda.getSaldo()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldThrow_whenWithdrawNonPositiveValue() {
        ContaResponse conta = criarConta("Gustavo Nunes", new BigDecimal("100.00"));

        assertThatThrownBy(() -> contaService.sacar(conta.getId(), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrow_whenWithdrawFromNonExistingAccount() {
        assertThatThrownBy(() -> contaService.sacar(999L, new BigDecimal("10.00")))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void shouldFindAccountById_whenExists() {
        ContaResponse criada = criarConta("Helena Ribeiro", new BigDecimal("77.77"));

        ContaResponse encontrada = contaService.buscarPorId(criada.getId());

        assertThat(encontrada.getId()).isEqualTo(criada.getId());
        assertThat(encontrada.getTitular()).isEqualTo("Helena Ribeiro");
    }

    @Test
    void shouldThrow_whenFindNonExistingAccountById() {
        assertThatThrownBy(() -> contaService.buscarPorId(999L))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void shouldListAllAccounts() {
        contaRepository.deleteAll();
        criarConta("Igor Sousa", new BigDecimal("10.00"));
        criarConta("Jana Lima", new BigDecimal("20.00"));

        assertThat(contaService.listarTodas()).hasSize(2);
    }

    @Test
    void shouldNotThrow_whenBasicOperationsSucceed() {
        assertDoesNotThrow(() -> {
            ContaResponse c = criarConta("Kelvin Oliveira", new BigDecimal("1000.00"));
            contaService.depositar(c.getId(), new BigDecimal("500.00"));
            contaService.sacar(c.getId(), new BigDecimal("200.00"));
            contaService.buscarPorId(c.getId());
        });
    }
}
