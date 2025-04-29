package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreatePaymentInfoDto;
import br.com.katsilis.mercadolance.dto.response.PaymentInfoResponseDto;
import br.com.katsilis.mercadolance.model.PaymentInfo;

import java.util.List;

public interface PaymentInfoService {
    List<PaymentInfoResponseDto> findAll();
    PaymentInfoResponseDto findById(Long id);
    void create(CreatePaymentInfoDto paymentInfo);
    void deleteById(Long id);
    PaymentInfoResponseDto paymentInfoToResponseDto(PaymentInfo paymentInfo);
}
