package br.com.katsilis.mercadolance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BidDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Auction ID is required")
    private Long auctionId;

    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be positive")
    private Double amount;
}
