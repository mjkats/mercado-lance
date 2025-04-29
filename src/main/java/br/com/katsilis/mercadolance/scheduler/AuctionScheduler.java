package br.com.katsilis.mercadolance.scheduler;


import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void closeExpiredAuctions() {
        List<Auction> expiredAuctions = auctionService.findExpiredAuctions();

        for (Auction auction : expiredAuctions) {
            auction.setStatus(AuctionStatus.FINISHED);
            auction.setUpdatedAt(LocalDateTime.now());
            UpdateAuctionDto updatedAuction = UpdateAuctionDto.builder().status(AuctionStatus.FINISHED).build();
            auctionService.update(auction.getId(), updatedAuction);

            log.info("Auction with id {} was closed.", auction.getId());
        }
    }
}