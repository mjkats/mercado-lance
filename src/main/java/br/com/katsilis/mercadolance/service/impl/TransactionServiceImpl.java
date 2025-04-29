package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateTransactionDto;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.model.Transaction;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.TransactionRepository;
import br.com.katsilis.mercadolance.service.BidService;
import br.com.katsilis.mercadolance.service.TransactionService;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BidService bidService;
    private final UserService userService;

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
    public Transaction create(CreateTransactionDto transaction) {
        LocalDateTime now = LocalDateTime.now();
        Bid winningBid = bidService.findById(transaction.getWinningBidId());
        User buyer = userService.findById(transaction.getBuyerId());

        Transaction newTransaction = Transaction
            .builder()
            .amount(transaction.getAmount())
            .winningBid(winningBid)
            .buyer(buyer)
            .transactionDate(now)
            .createdAt(now)
            .build();

        return transactionRepository.save(newTransaction);
    }

    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }
}
