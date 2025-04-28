package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.model.PaymentInfo;

import java.util.List;

public interface PaymentInfoService {
    List<PaymentInfo> findAll();
    PaymentInfo findById(Long id);
    PaymentInfo save(PaymentInfo paymentInfo);
    void deleteById(Long id);
}
