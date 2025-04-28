package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.model.Transaction;
import br.com.katsilis.mercadolance.repository.TransactionRepository;
import br.com.katsilis.mercadolance.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }
}
