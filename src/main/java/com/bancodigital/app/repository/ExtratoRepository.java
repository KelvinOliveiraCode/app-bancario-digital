package com.bancodigital.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancodigital.app.model.Extrato;

/**
 * Reposit\u00f3rio de persist\u00eancia de {@link Extrato}.
 */
@Repository
public interface ExtratoRepository extends JpaRepository<Extrato, Long> {

    List<Extrato> findByContaIdOrderByDataMovimentoDesc(Long contaId);
}
