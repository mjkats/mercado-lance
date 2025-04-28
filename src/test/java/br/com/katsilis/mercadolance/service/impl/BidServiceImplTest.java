package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import br.com.katsilis.mercadolance.service.RedisService;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @InjectMocks
    private BidServiceImpl bidService;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private AuctionService auctionService;

    @Mock
    private UserService userService;

    @Mock
    private RedisService redisService;

    @Test
    void findById_existingId_returnsBid() {
        Bid bid = new Bid();
        when(bidRepository.findById(1L)).thenReturn(Optional.of(bid));

        Bid result = bidService.findById(1L);

        assertNotNull(result);
        verify(bidRepository).findById(1L);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(bidRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bidService.findById(1L));
        verify(bidRepository).findById(1L);
    }

    @Test
    void getBids_byAuctionIdAndUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findByAuction_IdAndUser_Id(1L, 2L, pageable)).thenReturn(bids);

        Page<Bid> result = bidService.getBids(1L, 2L, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findByAuction_IdAndUser_Id(1L, 2L, pageable);
    }

    @Test
    void getBids_byUserIdOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findByUser_Id(2L, pageable)).thenReturn(bids);

        Page<Bid> result = bidService.getBids(null, 2L, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findByUser_Id(2L, pageable);
    }

    @Test
    void getBids_byAuctionIdOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findByAuction_Id(1L, pageable)).thenReturn(bids);

        Page<Bid> result = bidService.getBids(1L, null, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findByAuction_Id(1L, pageable);
    }

    @Test
    void getBids_noFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findAll(pageable)).thenReturn(bids);

        Page<Bid> result = bidService.getBids(null, null, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findAll(pageable);
    }

    @Test
    void delete_existingBid_deletesSuccessfully() {
        when(bidRepository.existsById(1L)).thenReturn(true);

        bidService.delete(1L);

        verify(bidRepository).existsById(1L);
        verify(bidRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingBid_throwsException() {
        when(bidRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bidService.delete(1L));
        verify(bidRepository).existsById(1L);
        verify(bidRepository, never()).deleteById(anyLong());
    }

    @Test
    void update_existingBid_updatesSuccessfully() {
        Bid existing = new Bid();
        existing.setBidTime(LocalDateTime.now());
        existing.setAmount(100.0);

        Bid updated = new Bid();
        updated.setBidTime(LocalDateTime.now());
        updated.setAmount(200.0);

        when(bidRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bidRepository.save(any(Bid.class))).thenReturn(existing);

        Bid result = bidService.update(1L, updated);

        assertEquals(200.0, result.getAmount());
        verify(bidRepository).findById(1L);
        verify(bidRepository).save(existing);
    }

    @Test
    void getLatestAuctionBid_existingAuction_returnsBid() {
        Bid bid = new Bid();
        when(bidRepository.findTopByAuction_IdOrderByAmountDesc(1L)).thenReturn(Optional.of(bid));

        Bid result = bidService.getLatestAuctionBid(1L);

        assertNotNull(result);
        verify(bidRepository).findTopByAuction_IdOrderByAmountDesc(1L);
    }

    @Test
    void getLatestAuctionBid_nonExistingAuction_throwsException() {
        when(bidRepository.findTopByAuction_IdOrderByAmountDesc(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bidService.getLatestAuctionBid(1L));
        verify(bidRepository).findTopByAuction_IdOrderByAmountDesc(1L);
    }
}