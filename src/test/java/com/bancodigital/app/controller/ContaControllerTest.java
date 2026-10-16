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

/**
 * Testes de integra\u00e7\u00e3o do {@link ContaController} via HTTP real
 * (TestRestTemplate com porta gerada pelo Spring + H2 em mem\u00f3ria).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContaControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String base() {
        return "http://localhost:" + port + "/contas";
    }

    @Test
    void shouldCreateConta_returns201() {
        ContaRequest request = new ContaRequest("Ana Teste", new BigDecimal("100.00"));

        ResponseEntity<ContaResponse> resposta = rest.postForEntity(base(), request,
                ContaResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getId()).isNotNull();
        assertThat(resposta.getBody().getTitular()).isEqualTo("Ana Teste");
        assertThat(resposta.getBody().getSaldo()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldCreateConta_returns400_whenTitularBlank() {
        ContaRequest request = new ContaRequest("   ", new BigDecimal("10.00"));

        ResponseEntity<ErrorResponse> resposta = rest.postForEntity(base(), request,
                ErrorResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getMensagem()).contains("titular");
    }

    @Test
    void shouldGetContaById_returns200_whenExists() {
        ContaRequest request = new ContaRequest("Bruno Teste", new BigDecimal("50.00"));
        ContaResponse criada = rest.postForObject(base(), request, ContaResponse.class);

        ResponseEntity<ContaResponse> resposta = rest.getForEntity(base() + "/" + criada.getId(),
                ContaResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().getTitular()).isEqualTo("Bruno Teste");
    }

    @Test
    void shouldGetContaById_returns404_whenNotFound() {
        ResponseEntity<ErrorResponse> resposta = rest.getForEntity(base() + "/999",
                ErrorResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody().getErro()).isEqualTo("CONTA_NAO_ENCONTRADA");
    }

    @Test
    void shouldListContas_returns200() {
        ResponseEntity<ContaResponse[]> antes = rest.getForEntity(base(),
                ContaResponse[].class);
        int tamanhoAntes = antes.getBody() == null ? 0 : antes.getBody().length;

        rest.postForEntity(base(), new ContaRequest("Carlos", new BigDecimal("10.00")),
                ContaResponse.class);
        rest.postForEntity(base(), new ContaRequest("Diana", new BigDecimal("20.00")),
                ContaResponse.class);

        ResponseEntity<ContaResponse[]> resposta = rest.getForEntity(base(),
                ContaResponse[].class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().length).isEqualTo(tamanhoAntes + 2);
    }

    @Test
    void shouldDepositar_returns200() {
        ContaResponse criada = rest.postForObject(base(), new ContaRequest("Edu",
                new BigDecimal("100.00")), ContaResponse.class);

        ResponseEntity<ContaResponse> resposta = rest.postForEntity(base() + "/" + criada.getId()
                + "/depositar", new ContaController.DepositoRequest(new BigDecimal("50.00")),
                ContaResponse.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().getSaldo()).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}
