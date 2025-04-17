package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
