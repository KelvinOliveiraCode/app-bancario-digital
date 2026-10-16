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
import com.bancodigital.app.dto.ExtratoResponse;

/**
 * Testes de integra\u00e7\u00e3o do {@link ExtratoController} via HTTP real.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExtratoControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String baseContas() {
        return "http://localhost:" + port + "/contas";
    }

    private String baseExtrato() {
        return "http://localhost:" + port + "/extrato";
    }

    private long criarConta(String titular, BigDecimal saldo) {
        ContaResponse c = rest.postForObject(baseContas(), new ContaRequest(titular, saldo),
                ContaResponse.class);
        return c.getId();
    }

    @Test
    void shouldGetExtrato_returns200_whenAccountExists() {
        long conta = criarConta("Extrato Teste", new BigDecimal("200.00"));
        rest.postForEntity(baseContas() + "/" + conta + "/depositar",
                new ContaController.DepositoRequest(new BigDecimal("100.00")), ContaResponse.class);

        ResponseEntity<ExtratoResponse[]> resposta = rest.getForEntity(baseExtrato() + "/" + conta,
                ExtratoResponse[].class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().length).isEqualTo(2); // abertura + deposito
    }

    @Test
    void shouldGetExtrato_returns404_whenAccountNotFound() {
        ResponseEntity<ErrorResponse> resposta = rest.getForEntity(baseExtrato() + "/999",
                ErrorResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody().getErro()).isEqualTo("CONTA_NAO_ENCONTRADA");
    }
}
