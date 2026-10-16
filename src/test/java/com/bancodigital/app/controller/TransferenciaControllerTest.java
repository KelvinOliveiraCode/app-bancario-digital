package com.bancodigital.app.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bancodigital.app.dto.ContaRequest;
import com.bancodigital.app.dto.ContaResponse;
import com.bancodigital.app.dto.ErrorResponse;
import com.bancodigital.app.dto.TransferenciaRequest;
import com.bancodigital.app.dto.TransferenciaResponse;

/**
 * Testes de integra\u00e7\u00e3o do {@link TransferenciaController} via HTTP real.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransferenciaControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String baseContas() {
        return "http://localhost:" + port + "/contas";
    }

    private String baseTransferencias() {
        return "http://localhost:" + port + "/transferencias";
    }

    private long criarConta(String titular, BigDecimal saldo) {
        ContaResponse c = rest.postForObject(baseContas(), new ContaRequest(titular, saldo),
                ContaResponse.class);
        return c.getId();
    }

    @Test
    void shouldTransfer_returns200_whenSufficientBalance() {
        long origem = criarConta("Fulano", new BigDecimal("1000.00"));
        long destino = criarConta("Ciclano", new BigDecimal("100.00"));

        ResponseEntity<TransferenciaResponse> resposta = rest.postForEntity(baseTransferencias(),
                new TransferenciaRequest(origem, destino, new BigDecimal("300.00")),
                TransferenciaResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getValor()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(resposta.getBody().getStatus()).isEqualTo("CONCLUIDA");
    }

    @Test
    void shouldTransfer_returns400_whenInsufficientBalance() {
        long origem = criarConta("Pobre", new BigDecimal("10.00"));
        long destino = criarConta("Rico", new BigDecimal("1000.00"));

        ResponseEntity<ErrorResponse> resposta = rest.postForEntity(baseTransferencias(),
                new TransferenciaRequest(origem, destino, new BigDecimal("500.00")),
                ErrorResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().getErro()).isEqualTo("SALDO_INSUFICIENTE");
    }

    @Test
    void shouldTransfer_returns400_whenSameAccount() {
        long conta = criarConta("Mesma", new BigDecimal("100.00"));

        ResponseEntity<ErrorResponse> resposta = rest.postForEntity(baseTransferencias(),
                new TransferenciaRequest(conta, conta, new BigDecimal("10.00")),
                ErrorResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().getErro()).isEqualTo("OPERACAO_INVALIDA");
    }

    @Test
    void shouldTransfer_returns404_whenOriginNotFound() {
        long destino = criarConta("Destino", new BigDecimal("10.00"));

        ResponseEntity<ErrorResponse> resposta = rest.postForEntity(baseTransferencias(),
                new TransferenciaRequest(999L, destino, new BigDecimal("10.00")),
                ErrorResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
