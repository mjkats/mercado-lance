package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateTransactionDto;
import br.com.katsilis.mercadolance.dto.response.TransactionResponseDto;
import br.com.katsilis.mercadolance.entity.Transaction;

import java.util.List;

public interface TransactionService {
    List<TransactionResponseDto> findAll();
    TransactionResponseDto findById(Long id);
    void create(CreateTransactionDto transaction);
    void deleteById(Long id);
    TransactionResponseDto transactionToResponseDto(Transaction transaction);
}
