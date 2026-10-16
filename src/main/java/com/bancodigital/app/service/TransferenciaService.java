package com.bancodigital.app.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancodigital.app.dto.TransferenciaRequest;
import com.bancodigital.app.dto.TransferenciaResponse;
import com.bancodigital.app.exception.ContaNaoEncontradaException;
import com.bancodigital.app.exception.OperacaoInvalidaException;
import com.bancodigital.app.exception.SaldoInsuficienteException;
import com.bancodigital.app.model.Extrato;
import com.bancodigital.app.model.Transferencia;
import com.bancodigital.app.repository.ContaRepository;
import com.bancodigital.app.repository.ExtratoRepository;
import com.bancodigital.app.repository.TransferenciaRepository;

/**
 * Servi\u00e7o de transfer\u00eancias entre contas.
 *
 * <p>Valida a existencia de origem e destino, que s\u00e3o contas distintas,
 * que o valor \u00e9 positivo e que a origem tem saldo suficiente, antes de
 * debitar a origem, creditar o destino e registrar o extrato de ambos.
 */
@Service
public class TransferenciaService {

    private final ContaRepository contaRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final ExtratoRepository extratoRepository;
    private final ContaService contaService;

    public TransferenciaService(ContaRepository contaRepository,
                                TransferenciaRepository transferenciaRepository,
                                ExtratoRepository extratoRepository,
                                ContaService contaService) {
        this.contaRepository = contaRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.extratoRepository = extratoRepository;
        this.contaService = contaService;
    }

    /**
     * Executa uma transfer\u00eancia entre contas.
     *
     * @param request dados da transfer\u00eancia
     * @return representa\u00e7\u00e3o da transfer\u00eancia conclu\u00edda
     * @throws ContaNaoEncontradaException se origem ou destino n\u00e3o existirem
     * @throws OperacaoInvalidaException   se origem == destino
     * @throws SaldoInsuficienteException  se saldo da origem insuficiente
     * @throws IllegalArgumentException    se valor <= 0
     */
    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest request) {
        validar(request);

        ContaOrigemDestino contas = buscarContas(request);

        if (contas.origem.getId().equals(contas.destino.getId())) {
            throw new OperacaoInvalidaException(
                    "Conta de origem e destino n\u00e3o podem ser a mesma");
        }

        if (contas.origem.getSaldo().compareTo(request.getValor()) < 0) {
            throw new SaldoInsuficienteException(
                    contas.origem.getId(), contas.origem.getSaldo(), request.getValor());
        }

        // Debita origem e credita destino
        contas.origem.setSaldo(contas.origem.getSaldo().subtract(request.getValor()));
        contas.destino.setSaldo(contas.destino.getSaldo().add(request.getValor()));
        contaRepository.save(contas.origem);
        contaRepository.save(contas.destino);

        // Registra transfer\u00eancia
        Transferencia transferencia = Transferencia.builder()
                .contaOrigemId(contas.origem.getId())
                .contaDestinoId(contas.destino.getId())
                .valor(request.getValor())
                .build();
        transferencia = transferenciaRepository.save(transferencia);

        // Registra extratos
        extratoRepository.save(Extrato.builder()
                .contaId(contas.origem.getId())
                .tipo(Extrato.TipoExtrato.DEBITO)
                .valor(request.getValor())
                .descricao("Transfer\u00eancia para conta " + contas.destino.getId())
                .build());
        extratoRepository.save(Extrato.builder()
                .contaId(contas.destino.getId())
                .tipo(Extrato.TipoExtrato.CREDITO)
                .valor(request.getValor())
                .descricao("Transfer\u00eancia recebida da conta " + contas.origem.getId())
                .build());

        return toResponse(transferencia);
    }

    private void validar(TransferenciaRequest request) {
        if (request.getValor() == null || request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transfer\u00eancia deve ser maior que zero");
        }
    }

    private ContaOrigemDestino buscarContas(TransferenciaRequest request) {
        var origem = contaRepository.findById(request.getContaOrigemId())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.getContaOrigemId()));
        var destino = contaRepository.findById(request.getContaDestinoId())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.getContaDestinoId()));
        return new ContaOrigemDestino(origem, destino);
    }

    private TransferenciaResponse toResponse(Transferencia t) {
        return TransferenciaResponse.builder()
                .id(t.getId())
                .contaOrigemId(t.getContaOrigemId())
                .contaDestinoId(t.getContaDestinoId())
                .valor(t.getValor())
                .dataTransferencia(t.getDataTransferencia())
                .status(t.getStatus().name())
                .build();
    }

    /** Par de contas origem/destino carregadas do reposit\u00f3rio. */
    private record ContaOrigemDestino(com.bancodigital.app.model.Conta origem,
                                      com.bancodigital.app.model.Conta destino) {
    }
}
