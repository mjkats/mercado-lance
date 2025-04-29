package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreatePaymentInfoDto;
import br.com.katsilis.mercadolance.dto.response.PaymentInfoResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.PaymentInfoNotFoundException;
import br.com.katsilis.mercadolance.model.PaymentInfo;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.PaymentInfoRepository;
import br.com.katsilis.mercadolance.service.PaymentInfoService;
import br.com.katsilis.mercadolance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentInfoServiceImpl implements PaymentInfoService {

    private final PaymentInfoRepository paymentInfoRepository;
    private final UserService userService;

    @Override
    public List<PaymentInfoResponseDto> findAll() {
        log.info("Fetching all payment infos");

        try {
            List<PaymentInfo> paymentInfos = paymentInfoRepository.findAll();
            List<PaymentInfoResponseDto> response = paymentInfos.stream().map(this::paymentInfoToResponseDto).toList();

            log.info("Fetched payment infos: {}", paymentInfos);
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching all payment infos", e);
        }
    }

    @Override
    public PaymentInfoResponseDto findById(Long id) {
        log.info("Fetching payment info by id: {}", id);

        try {
            PaymentInfo paymentInfo = paymentInfoRepository.findById(id)
                .orElseThrow(() -> new PaymentInfoNotFoundException("PaymentInfo with id " + id + " not found"));

            PaymentInfoResponseDto response = paymentInfoToResponseDto(paymentInfo);
            log.info("Fetched payment info: {}", paymentInfo);
            return response;
        } catch (PaymentInfoNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching payment info with id " + id, e);
        }
    }

    @Override
    public void create(CreatePaymentInfoDto paymentInfo) {
        log.info("Creating new payment info with data: {}", paymentInfo);

        try {
            LocalDateTime now = LocalDateTime.now();
            User user = userService.findOriginalById(paymentInfo.getUserId());

            PaymentInfo newPaymentInfo = PaymentInfo
                .builder()
                .amount(paymentInfo.getAmount())
                .paymentMethod(paymentInfo.getPaymentMethod())
                .user(user)
                .paymentDate(now)
                .build();

            paymentInfoRepository.save(newPaymentInfo);
            log.info("Successfully created payment info: {}", newPaymentInfo);
        } catch (Exception e) {
            throw new DatabaseException("Error while creating payment info", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting payment info by id: {}", id);

        try {
            paymentInfoRepository.deleteById(id);
            log.info("Successfully deleted payment info with id: {}", id);
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting payment info with id " + id, e);
        }
    }

    @Override
    public PaymentInfoResponseDto paymentInfoToResponseDto(PaymentInfo paymentInfo) {
        UserResponseDto userResponseDto = userService.userToResponseDto(paymentInfo.getUser());
        return new PaymentInfoResponseDto(userResponseDto, paymentInfo.getPaymentMethod(), paymentInfo.getAmount(), paymentInfo.getPaymentDate());
    }
}
