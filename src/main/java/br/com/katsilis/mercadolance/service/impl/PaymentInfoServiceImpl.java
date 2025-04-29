package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreatePaymentInfoDto;
import br.com.katsilis.mercadolance.model.PaymentInfo;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.PaymentInfoRepository;
import br.com.katsilis.mercadolance.service.PaymentInfoService;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentInfoServiceImpl implements PaymentInfoService {

    private final PaymentInfoRepository paymentInfoRepository;
    private final UserService userService;

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
    public PaymentInfo create(CreatePaymentInfoDto paymentInfo) {
        LocalDateTime now = LocalDateTime.now();
        User user = userService.findById(paymentInfo.getUserId());

        PaymentInfo newPaymentInfo = PaymentInfo
            .builder()
            .amount(paymentInfo.getAmount())
            .paymentMethod(paymentInfo.getPaymentMethod())
            .user(user)
            .paymentDate(now)
            .build();

        return paymentInfoRepository.save(newPaymentInfo);
    }

    @Override
    public void deleteById(Long id) {
        paymentInfoRepository.deleteById(id);
    }
}
