package com.moura.sistemapagamentosbackend.service.transaction;

import com.moura.sistemapagamentosbackend.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
