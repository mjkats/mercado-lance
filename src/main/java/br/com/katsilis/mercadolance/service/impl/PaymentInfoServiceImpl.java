package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.repository.PaymentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentInfoServiceImpl {

    private final PaymentInfoRepository paymentInfoRepository;
}
