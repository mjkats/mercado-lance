package br.com.katsilis.mercadolance.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BidResponseDto {
    private UserResponseDto user;
    private AuctionResponseDto auction;
    private double amount;
}
