package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> findAll();
    Transaction findById(Long id);
    Transaction save(Transaction transaction);
    void deleteById(Long id);
}
