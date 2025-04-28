package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.model.PaymentInfo;
import br.com.katsilis.mercadolance.service.PaymentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-info")
@RequiredArgsConstructor
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;

    @GetMapping
    public List<PaymentInfo> getAll() {
        return paymentInfoService.findAll();
    }

    @GetMapping("/{id}")
    public PaymentInfo getById(@PathVariable Long id) {
        return paymentInfoService.findById(id);
    }

    @PostMapping
    public PaymentInfo create(@RequestBody PaymentInfo paymentInfo) {
        return paymentInfoService.save(paymentInfo);
    }

    @PutMapping("/{id}")
    public PaymentInfo update(@PathVariable Long id, @RequestBody PaymentInfo paymentInfo) {
        paymentInfo.setId(id);
        return paymentInfoService.save(paymentInfo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        paymentInfoService.deleteById(id);
    }
}
