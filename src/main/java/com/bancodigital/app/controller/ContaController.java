package com.bancodigital.app.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bancodigital.app.dto.ContaRequest;
import com.bancodigital.app.dto.ContaResponse;
import com.bancodigital.app.service.ContaService;

import jakarta.validation.Valid;
import java.net.URI;

/**
 * Controlador REST de contas.
 *
 * <p>Endpoints: criar, buscar por ID e listar contas.
 */
@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> criar(@RequestBody @Valid ContaRequest request) {
        ContaResponse resposta = contaService.criar(request);
        return ResponseEntity.created(URI.create("/contas/" + resposta.getId())).body(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<java.util.List<ContaResponse>> listarTodas() {
        return ResponseEntity.ok(contaService.listarTodas());
    }

    @PostMapping("/{id}/depositar")
    public ResponseEntity<ContaResponse> depositar(@PathVariable Long id,
                                                   @RequestBody DepositoRequest request) {
        return ResponseEntity.ok(contaService.depositar(id, request.valor()));
    }

    @PostMapping("/{id}/sacar")
    public ResponseEntity<ContaResponse> sacar(@PathVariable Long id,
                                               @RequestBody SaqueRequest request) {
        return ResponseEntity.ok(contaService.sacar(id, request.valor()));
    }

    public record DepositoRequest(BigDecimal valor) {
    }

    public record SaqueRequest(BigDecimal valor) {
    }
}
