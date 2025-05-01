package br.com.katsilis.mercadolance.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class BidResponseDto {
    private UserResponseDto user;
    private AuctionResponseDto auction;
    private double amount;
}
