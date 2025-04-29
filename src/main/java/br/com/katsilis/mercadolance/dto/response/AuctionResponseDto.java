package br.com.katsilis.mercadolance.dto.response;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AuctionResponseDto {
    private String title;
    private String description;
    private ProductResponseDto product;
    private UserResponseDto createdBy;
    private double startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
}
