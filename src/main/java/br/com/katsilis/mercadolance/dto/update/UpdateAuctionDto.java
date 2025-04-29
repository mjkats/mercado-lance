package br.com.katsilis.mercadolance.dto.update;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UpdateAuctionDto {

    private String title;

    private String description;

    private LocalDateTime endTime;

    private AuctionStatus status;
}

