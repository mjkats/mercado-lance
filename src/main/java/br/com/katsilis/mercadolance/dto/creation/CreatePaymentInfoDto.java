package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePaymentInfoDto {

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Payment method is required.")
    private String paymentMethod;

    @Positive(message = "Amount must be greater than zero.")
    private double amount;
}