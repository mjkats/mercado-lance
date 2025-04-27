package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.model.PaymentInfo;
import br.com.katsilis.mercadolance.repository.PaymentInfoRepository;
import br.com.katsilis.mercadolance.service.PaymentInfoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentInfoServiceImpl implements PaymentInfoService {

    private final PaymentInfoRepository paymentInfoRepository;

    @Override
    public List<PaymentInfo> findAll() {
        return paymentInfoRepository.findAll();
    }

    @Override
    public PaymentInfo findById(Long id) {
        return paymentInfoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("PaymentInfo not found"));
    }

    @Override
    public PaymentInfo save(PaymentInfo paymentInfo) {
        return paymentInfoRepository.save(paymentInfo);
    }

    @Override
    public void deleteById(Long id) {
        paymentInfoRepository.deleteById(id);
    }
}
