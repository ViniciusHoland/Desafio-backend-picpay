package com.picpaysimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.picpaysimplificado.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
