package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class CreateTransactionDto {

    @NotNull(message = "Winning bid ID is required.")
    private Long winningBidId;

    @NotNull(message = "Buyer user ID is required.")
    private Long buyerId;

    @Positive(message = "Amount must be greater than zero.")
    private double amount;
}