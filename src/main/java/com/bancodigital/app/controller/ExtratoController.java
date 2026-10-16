package com.bancodigital.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bancodigital.app.dto.ExtratoResponse;
import com.bancodigital.app.service.ExtratoService;

import java.util.List;

/**
 * Controlador REST de extrato.
 */
@RestController
@RequestMapping("/extrato")
public class ExtratoController {

    private final ExtratoService extratoService;

    public ExtratoController(ExtratoService extratoService) {
        this.extratoService = extratoService;
    }

    @GetMapping("/{contaId}")
    public ResponseEntity<List<ExtratoResponse>> listarPorConta(@PathVariable Long contaId) {
        return ResponseEntity.ok(extratoService.listarPorConta(contaId));
    }
}
