package br.com.katsilis.mercadolance.scheduler;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.entity.Auction;
import br.com.katsilis.mercadolance.service.AuctionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerTest {

    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

    @Test
    void testCloseExpiredAuctions_WhenThereAreExpiredAuctions() {
        // Arrange
        Auction auction1 = Auction.builder()
            .id(1L)
            .title("Auction 1")
            .status(AuctionStatus.ACTIVE)
            .endTime(LocalDateTime.now().minusMinutes(1))
            .build();

        Auction auction2 = Auction.builder()
            .id(2L)
            .title("Auction 2")
            .status(AuctionStatus.ACTIVE)
            .endTime(LocalDateTime.now().minusMinutes(2))
            .build();

        List<Auction> expiredAuctions = List.of(auction1, auction2);
        when(auctionService.findExpiredAuctions()).thenReturn(expiredAuctions);

        auctionScheduler.closeExpiredAuctions();

        assertEquals(AuctionStatus.FINISHED, auction1.getStatus());
        assertEquals(AuctionStatus.FINISHED, auction2.getStatus());

        verify(auctionService).findExpiredAuctions();
        verify(auctionService).update(eq(1L), argThat(dto -> dto.getStatus() == AuctionStatus.FINISHED));
        verify(auctionService).update(eq(2L), argThat(dto -> dto.getStatus() == AuctionStatus.FINISHED));
    }

    @Test
    void testCloseExpiredAuctions_WhenNoExpiredAuctions() {
        when(auctionService.findExpiredAuctions()).thenReturn(List.of());

        auctionScheduler.closeExpiredAuctions();

        verify(auctionService).findExpiredAuctions();
        verify(auctionService, never()).update(anyLong(), any());
    }
}
