package br.com.katsilis.mercadolance.dto.response;

import br.com.katsilis.mercadolance.enums.PaymentMethod;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PaymentInfoResponseDto {
    private UserResponseDto user;
    private PaymentMethod paymentMethod;
    private double amount;
    private LocalDateTime paymentDate;
}
