package com.bancodigital.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bancodigital.app.dto.ExtratoResponse;
import com.bancodigital.app.exception.ContaNaoEncontradaException;
import com.bancodigital.app.model.Conta;
import com.bancodigital.app.model.Extrato;
import com.bancodigital.app.repository.ContaRepository;
import com.bancodigital.app.repository.ExtratoRepository;

/**
 * Servi\u00e7o de consulta de extrato por conta.
 */
@Service
public class ExtratoService {

    private final ContaRepository contaRepository;
    private final ExtratoRepository extratoRepository;

    public ExtratoService(ContaRepository contaRepository,
                          ExtratoRepository extratoRepository) {
        this.contaRepository = contaRepository;
        this.extratoRepository = extratoRepository;
    }

    /**
     * Lista os lan\u00e7amentos do extrato de uma conta, do mais recente para o
     * mais antigo.
     *
     * @param contaId ID da conta
     * @return lista de lan\u00e7amentos do extrato
     * @throws ContaNaoEncontradaException se a conta n\u00e3o existir
     */
    public List<ExtratoResponse> listarPorConta(Long contaId) {
        if (!contaRepository.existsById(contaId)) {
            throw new ContaNaoEncontradaException(contaId);
        }
        return extratoRepository.findByContaIdOrderByDataMovimentoDesc(contaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ExtratoResponse toResponse(Extrato e) {
        return ExtratoResponse.builder()
                .id(e.getId())
                .contaId(e.getContaId())
                .tipo(e.getTipo().name())
                .valor(e.getValor())
                .descricao(e.getDescricao())
                .dataMovimento(e.getDataMovimento())
                .build();
    }
}
