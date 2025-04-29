package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.creation.CreateTransactionDto;
import br.com.katsilis.mercadolance.model.Transaction;
import br.com.katsilis.mercadolance.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable Long id) {
        return transactionService.findById(id);
    }

    @PostMapping
    public Transaction create(@RequestBody @Valid CreateTransactionDto transaction) {
        return transactionService.create(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        transactionService.deleteById(id);
    }
}