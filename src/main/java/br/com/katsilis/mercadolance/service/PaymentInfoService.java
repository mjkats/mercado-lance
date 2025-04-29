package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreatePaymentInfoDto;
import br.com.katsilis.mercadolance.model.PaymentInfo;

import java.util.List;

public interface PaymentInfoService {
    List<PaymentInfo> findAll();
    PaymentInfo findById(Long id);
    PaymentInfo create(CreatePaymentInfoDto paymentInfo);
    void deleteById(Long id);
}
