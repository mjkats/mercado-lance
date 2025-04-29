package br.com.katsilis.mercadolance.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransactionResponseDto {
    private BidResponseDto winningBid;
    private UserResponseDto buyer;
    private double amount;
    private LocalDateTime transactionDate;
}
