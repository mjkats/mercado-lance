package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.creation.CreatePaymentInfoDto;
import br.com.katsilis.mercadolance.dto.response.PaymentInfoResponseDto;
import br.com.katsilis.mercadolance.model.PaymentInfo;
import br.com.katsilis.mercadolance.service.PaymentInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-info")
@RequiredArgsConstructor
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;

    @GetMapping
    public ResponseEntity<List<PaymentInfoResponseDto>> getAll() {
        return ResponseEntity.ok().body(paymentInfoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentInfoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(paymentInfoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreatePaymentInfoDto paymentInfo) {
        paymentInfoService.create(paymentInfo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentInfoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
