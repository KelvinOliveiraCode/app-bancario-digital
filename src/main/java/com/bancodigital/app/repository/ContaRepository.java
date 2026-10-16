package com.bancodigital.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancodigital.app.model.Conta;

/**
 * Reposit\u00f3rio de persist\u00eancia de {@link Conta}.
 */
@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
}
