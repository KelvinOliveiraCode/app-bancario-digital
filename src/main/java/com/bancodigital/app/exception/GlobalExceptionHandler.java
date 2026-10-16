package com.bancodigital.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bancodigital.app.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Tratador global de exce\u00e7\u00f5es da API. Converte exce\u00e7\u00f5es de
 * neg\u00f3cio e de valida\u00e7\u00e3o em respostas HTTP padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> contaNaoEncontrada(ContaNaoEncontradaException ex) {
        return erro(HttpStatus.NOT_FOUND, "CONTA_NAO_ENCONTRADA", ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> saldoInsuficiente(SaldoInsuficienteException ex) {
        return erro(HttpStatus.BAD_REQUEST, "SALDO_INSUFICIENTE", ex.getMessage());
    }

    @ExceptionHandler(OperacaoInvalidaException.class)
    public ResponseEntity<ErrorResponse> operacaoInvalida(OperacaoInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, "OPERACAO_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> argumentoInvalido(IllegalArgumentException ex) {
        return erro(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacaoFalhou(MethodArgumentNotValidException ex) {
        String mensagens = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return erro(HttpStatus.BAD_REQUEST, "VALIDACAO_FALHOU", mensagens);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperada(Exception ex) {
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO", ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> erro(HttpStatus status, String erro, String mensagem) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro(erro)
                .mensagem(mensagem)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
