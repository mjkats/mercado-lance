package br.com.katsilis.mercadolance.dto.creation;

import br.com.katsilis.mercadolance.annotation.ValidEnum;
import br.com.katsilis.mercadolance.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CreatePaymentInfoDto {

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Payment method is required.")
    @ValidEnum(message = "Invalid payment method.")
    private PaymentMethod paymentMethod;

    @Positive(message = "Amount must be greater than zero.")
    private double amount;
}