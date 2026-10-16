package com.bancodigital.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancodigital.app.model.Transferencia;

/**
 * Reposit\u00f3rio de persist\u00eancia de {@link Transferencia}.
 */
@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
}
