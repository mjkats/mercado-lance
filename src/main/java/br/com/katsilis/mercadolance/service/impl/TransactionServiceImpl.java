package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateTransactionDto;
import br.com.katsilis.mercadolance.dto.response.BidResponseDto;
import br.com.katsilis.mercadolance.dto.response.TransactionResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.TransactionNotFoundException;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.model.Transaction;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.TransactionRepository;
import br.com.katsilis.mercadolance.service.BidService;
import br.com.katsilis.mercadolance.service.TransactionService;
import br.com.katsilis.mercadolance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BidService bidService;
    private final UserService userService;

    @Override
    public List<TransactionResponseDto> findAll() {
        log.info("Fetching all transactions");

        try {
            List<Transaction> transactions = transactionRepository.findAll();
            List<TransactionResponseDto> response = transactions.stream()
                .map(this::transactionToResponseDto)
                .toList();

            log.info("Fetched transactions: {}", transactions);
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching all transactions", e);
        }
    }

    @Override
    public TransactionResponseDto findById(Long id) {
        log.info("Fetching transaction by id: {}", id);

        try {
            Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + id + " not found"));

            TransactionResponseDto response = transactionToResponseDto(transaction);
            log.info("Fetched transaction: {}", transaction);
            return response;
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching transaction with id " + id, e);
        }
    }

    @Override
    public void create(CreateTransactionDto transaction) {
        log.info("Creating new transaction with data: {}", transaction);

        try {
            LocalDateTime now = LocalDateTime.now();
            Bid winningBid = bidService.findOriginalById(transaction.getWinningBidId());
            User buyer = userService.findOriginalById(transaction.getBuyerId());

            Transaction newTransaction = Transaction
                .builder()
                .amount(transaction.getAmount())
                .winningBid(winningBid)
                .buyer(buyer)
                .transactionDate(now)
                .createdAt(now)
                .build();

            transactionRepository.save(newTransaction);
            log.info("Successfully created transaction: {}", newTransaction);
        } catch (Exception e) {
            throw new DatabaseException("Error while creating transaction", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting transaction by id: {}", id);

        try {
            transactionRepository.deleteById(id);
            log.info("Successfully deleted transaction with id: {}", id);
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting transaction with id " + id, e);
        }
    }

    @Override
    public TransactionResponseDto transactionToResponseDto(Transaction transaction) {
        BidResponseDto bidResponseDto = bidService.bidToResponseDto(transaction.getWinningBid());
        UserResponseDto userResponseDto = userService.userToResponseDto(transaction.getBuyer());

        return new TransactionResponseDto(bidResponseDto, userResponseDto, transaction.getAmount(), transaction.getTransactionDate());
    }
}
