package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.dto.response.BidResponseDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.BidNotFoundException;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import br.com.katsilis.mercadolance.service.RedisService;
import br.com.katsilis.mercadolance.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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

        BidResponseDto result = bidService.findById(1L);

        assertNotNull(result);
        verify(bidRepository).findById(1L);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(bidRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BidNotFoundException.class, () -> bidService.findById(1L));
        verify(bidRepository).findById(1L);
    }

    @Test
    void getBids_byAuctionIdAndUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findByAuction_IdAndUser_Id(1L, 2L, pageable)).thenReturn(bids);

        Page<BidResponseDto> result = bidService.getBids(1L, 2L, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findByAuction_IdAndUser_Id(1L, 2L, pageable);
    }

    @Test
    void getBids_byUserIdOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findByUser_Id(2L, pageable)).thenReturn(bids);

        Page<BidResponseDto> result = bidService.getBids(null, 2L, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findByUser_Id(2L, pageable);
    }

    @Test
    void getBids_byAuctionIdOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findByAuction_Id(1L, pageable)).thenReturn(bids);

        Page<BidResponseDto> result = bidService.getBids(1L, null, pageable);

        assertEquals(1, result.getContent().size());
        verify(bidRepository).findByAuction_Id(1L, pageable);
    }

    @Test
    void getBids_noFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bid> bids = new PageImpl<>(List.of(new Bid()));
        when(bidRepository.findAll(pageable)).thenReturn(bids);

        Page<BidResponseDto> result = bidService.getBids(null, null, pageable);

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

        assertThrows(DatabaseException.class, () -> bidService.delete(1L));
        verify(bidRepository).existsById(1L);
        verify(bidRepository, never()).deleteById(anyLong());
    }

    @Test
    void getLatestAuctionBid_existingActiveAuction_returnsBid() {
        Bid bid = new Bid();
        when(bidRepository.findTopByAuction_IdAndAuction_StatusOrderByAmountDesc(1L, AuctionStatus.ACTIVE)).thenReturn(Optional.of(bid));

        BidResponseDto result = bidService.getLatestActiveAuctionBid(1L);

        assertNotNull(result);
        verify(bidRepository).findTopByAuction_IdAndAuction_StatusOrderByAmountDesc(1L, AuctionStatus.ACTIVE);
    }

    @Test
    void getLatestAuctionBid_nonExistingActiveAuction_throwsException() {
        when(bidRepository.findTopByAuction_IdAndAuction_StatusOrderByAmountDesc(1L, AuctionStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(BidNotFoundException.class, () -> bidService.getLatestActiveAuctionBid(1L));
        verify(bidRepository).findTopByAuction_IdAndAuction_StatusOrderByAmountDesc(1L, AuctionStatus.ACTIVE);
    }

    @Test
    void create_validBid_savesBidSuccessfully() {
        CreateBidDto bidDto = new CreateBidDto();
        bidDto.setAuctionId(1L);
        bidDto.setUserId(2L);
        bidDto.setAmount(150.0);

        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setCreatedBy(User.builder().id(20L).build());

        User user = new User();
        user.setId(2L);

        User previousBidder = new User();
        previousBidder.setId(3L);

        Bid previousBid = Bid.builder()
            .id(10L)
            .amount(100.0)
            .user(previousBidder)
            .build();

        when(auctionService.findOriginalByIdAndStatus(1L, AuctionStatus.ACTIVE)).thenReturn(auction);
        when(userService.findOriginalById(2L)).thenReturn(user);
        when(bidRepository.findByAuction_Id(1L)).thenReturn(List.of(previousBid));
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> {
            Bid bid = invocation.getArgument(0);
            bid.setId(10L);
            return bid;
        });

        when(redisService.getAuctionBidKeys(bidDto.getAuctionId())).thenReturn(Set.of("auction:" + bidDto.getAuctionId() + ":pendingBid:user:*"));
        when(redisService.getAuctionBidValues(bidDto.getAuctionId())).thenReturn(List.of(Map.of(
            "auctionId", String.valueOf(auction.getId()),
            "userId", String.valueOf(user.getId()),
            "createdAt", "2025-05-05T00:00:00"
        )));

        bidService.create(bidDto);

        verify(bidRepository).save(any(Bid.class));
        verify(bidRepository).findByAuction_Id(anyLong());
    }
}