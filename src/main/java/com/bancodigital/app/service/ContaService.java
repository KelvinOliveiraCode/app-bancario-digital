package com.bancodigital.app.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancodigital.app.dto.ContaRequest;
import com.bancodigital.app.dto.ContaResponse;
import com.bancodigital.app.exception.ContaNaoEncontradaException;
import com.bancodigital.app.exception.SaldoInsuficienteException;
import com.bancodigital.app.model.Conta;
import com.bancodigital.app.repository.ContaRepository;
import com.bancodigital.app.repository.ExtratoRepository;
import com.bancodigital.app.model.Extrato;

/**
 * Servi\u00e7o de contas.
 *
 * <p>Respons\u00e1vel por criar contas, creditara (dep\u00f3sito) e debitara
 * (saque) saldos, garantindo consist\u00eancia de transa\u00e7\u00e3o.
 */
@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final ExtratoRepository extratoRepository;

    public ContaService(ContaRepository contaRepository,
                        ExtratoRepository extratoRepository) {
        this.contaRepository = contaRepository;
        this.extratoRepository = extratoRepository;
    }

    /**
     * Cria uma nova conta com o titular e saldo inicial informados.
     *
     * @param request dados de entrada da conta
     * @return representa\u00e7\u00e3o da conta criada
     */
    @Transactional
    public ContaResponse criar(ContaRequest request) {
        Conta conta = Conta.builder()
                .titular(request.getTitular())
                .saldo(request.getSaldoInicial())
                .build();
        conta = contaRepository.save(conta);

        if (request.getSaldoInicial().compareTo(BigDecimal.ZERO) > 0) {
            registrarExtrato(conta.getId(), Extrato.TipoExtrato.CREDITO,
                    request.getSaldoInicial(), "Abertura de conta");
        }

        return toResponse(conta);
    }

    /**
     * Busca uma conta pelo ID.
     *
     * @param id ID da conta
     * @return representa\u00e7\u00e3o da conta encontrada
     * @throws ContaNaoEncontradaException se n\u00e3o existir
     */
    public ContaResponse buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
        return toResponse(conta);
    }

    /**
     * Lista todas as contas cadastradas.
     *
     * @return lista de representa\u00e7\u00f5es de contas
     */
    public java.util.List<ContaResponse> listarTodas() {
        return contaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Dep\u00f3sito de valor na conta.
     *
     * @param id   ID da conta
     * @param valor valor a depositar (deve ser maior que zero)
     * @return representa\u00e7\u00e3o da conta atualizada
     * @throws ContaNaoEncontradaException  se n\u00e3o existir
     * @throws IllegalArgumentException     se valor <= 0
     */
    @Transactional
    public ContaResponse depositar(Long id, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do dep\u00f3sito deve ser maior que zero");
        }
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
        registrarExtrato(id, Extrato.TipoExtrato.CREDITO, valor, "Dep\u00f3sito");
        return toResponse(conta);
    }

    /**
     * Saque de valor da conta.
     *
     * @param id   ID da conta
     * @param valor valor a sacar (deve ser maior que zero)
     * @return representa\u00e7\u00e3o da conta atualizada
     * @throws ContaNaoEncontradaException se n\u00e3o existir
     * @throws SaldoInsuficienteException  se saldo menor que o valor
     * @throws IllegalArgumentException    se valor <= 0
     */
    @Transactional
    public ContaResponse sacar(Long id, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero");
        }
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(id, conta.getSaldo(), valor);
        }
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
        registrarExtrato(id, Extrato.TipoExtrato.DEBITO, valor, "Saque");
        return toResponse(conta);
    }

    private ContaResponse toResponse(Conta conta) {
        return ContaResponse.builder()
                .id(conta.getId())
                .titular(conta.getTitular())
                .saldo(conta.getSaldo())
                .criadoEm(conta.getCriadoEm())
                .build();
    }

    private void registrarExtrato(Long contaId, Extrato.TipoExtrato tipo,
                                  BigDecimal valor, String descricao) {
        Extrato lancamento = Extrato.builder()
                .contaId(contaId)
                .tipo(tipo)
                .valor(valor)
                .descricao(descricao)
                .build();
        extratoRepository.save(lancamento);
    }
}
