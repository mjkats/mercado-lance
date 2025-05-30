package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.creation.CreateTransactionDto;
import br.com.katsilis.mercadolance.dto.response.TransactionResponseDto;
import br.com.katsilis.mercadolance.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateTransactionDto transaction) {
        transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
