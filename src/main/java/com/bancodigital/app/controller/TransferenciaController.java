package com.bancodigital.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bancodigital.app.dto.TransferenciaRequest;
import com.bancodigital.app.dto.TransferenciaResponse;
import com.bancodigital.app.service.TransferenciaService;

import jakarta.validation.Valid;

/**
 * Controlador REST de transfer\u00eancias.
 */
@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponse> transferir(
            @RequestBody @Valid TransferenciaRequest request) {
        TransferenciaResponse resposta = transferenciaService.transferir(request);
        return ResponseEntity.ok(resposta);
    }
}
