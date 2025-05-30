package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateAuctionDto;
import br.com.katsilis.mercadolance.dto.response.*;
import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.exception.illegalargument.AuctionIllegalArgumentException;
import br.com.katsilis.mercadolance.exception.notfound.AuctionNotFoundException;
import br.com.katsilis.mercadolance.entity.Auction;
import br.com.katsilis.mercadolance.entity.Bid;
import br.com.katsilis.mercadolance.entity.Product;
import br.com.katsilis.mercadolance.entity.User;
import br.com.katsilis.mercadolance.repository.AuctionRepository;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.ProductService;
import br.com.katsilis.mercadolance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {

    @InjectMocks
    private AuctionServiceImpl auctionService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private BidRepository bidRepository;

    private Auction auction;
    private CreateAuctionDto createAuctionDto;
    private UpdateAuctionDto updateAuctionDto;

    @BeforeEach
    void setUp() {
        auction = Auction.builder()
            .id(1L)
            .title("Auction 1")
            .description("Description 1")
            .startingPrice(100.0)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(1))
            .status(AuctionStatus.ACTIVE)
            .build();

        createAuctionDto = new CreateAuctionDto(
            "Auction 1", "Description 1", 1L,
            1L, 100.00, LocalDateTime.now().plusDays(1)
        );

        updateAuctionDto = new UpdateAuctionDto("Updated Auction", "Updated Description", auction.getEndTime().plusDays(1), AuctionStatus.ACTIVE);
    }

    @Test
    void testFindAll() {
        when(auctionRepository.findAll()).thenReturn(List.of(auction));

        List<AuctionResponseDto> auctions = auctionService.findAll();

        assertNotNull(auctions);
        assertEquals(1, auctions.size());
        assertEquals(auction.getTitle(), auctions.getFirst().getTitle());
    }

    @Test
    void testGetAuctionsWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auctionRepository.findByStatusAndProduct_NameContainingIgnoreCase(any(), any(), eq(pageable)))
            .thenReturn(Page.empty());

        Page<AuctionResponseDto> result = auctionService.getAuctions(AuctionStatus.ACTIVE, "Product 1", pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByStatus() {
        when(auctionRepository.findByStatus(any(AuctionStatus.class))).thenReturn(List.of(auction));
        when(bidRepository.findTop1ByAuction_IdAndAuction_StatusOrderByAmountDesc(anyLong(), any(AuctionStatus.class))).thenReturn(Optional.ofNullable(Bid.builder().amount(10.0).build()));

        List<AuctionBidResponseDto> auctions = auctionService.findByStatus(AuctionStatus.ACTIVE);

        assertNotNull(auctions);
        assertEquals(1, auctions.size());
        assertEquals(auction.getTitle(), auctions.getFirst().getTitle());
    }

    @Test
    void testFindById() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        AuctionBidResponseDto result = auctionService.findById(1L);

        assertNotNull(result);
        assertEquals(auction.getTitle(), result.getTitle());
    }

    @Test
    void testCreateAuction() {
        when(productService.findOriginalById(1L)).thenReturn(new Product());
        when(userService.findOriginalById(1L)).thenReturn(new User());
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        auctionService.create(createAuctionDto);

        verify(auctionRepository, times(1)).save(any(Auction.class));
    }

    @Test
    void testCreateAuctionEndTimeBeforeCurrent() {
        CreateAuctionDto invalidDto = new CreateAuctionDto(
            "Auction", "Description", 1L,
            1L, 100.00, LocalDateTime.now().minusDays(1)
        );

        assertThrows(AuctionIllegalArgumentException.class, () -> auctionService.create(invalidDto));
    }

    @Test
    void testUpdateAuction() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        auctionService.update(1L, updateAuctionDto);

        verify(auctionRepository, times(1)).save(any(Auction.class));
        assertEquals("Updated Auction", auction.getTitle());
    }

    @Test
    void testDeleteAuction() {
        Auction mockAuction = new Auction();
        mockAuction.setId(1L);

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        auctionService.delete(1L);

        verify(bidRepository, times(1)).deleteByAuctionId(1L);
        verify(auctionRepository, times(1)).delete(mockAuction);
    }

    @Test
    void testDeleteAuctionNotFound() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuctionNotFoundException.class, () -> auctionService.delete(1L));

        verify(bidRepository, never()).deleteByAuctionId(anyLong());
        verify(auctionRepository, never()).delete(any(Auction.class));
    }

    @Test
    void testFindExpiredAuctions() {
        when(auctionRepository.findByStatusAndEndTimeBefore(any(AuctionStatus.class), any(LocalDateTime.class)))
            .thenReturn(List.of(auction));

        List<Auction> expiredAuctions = auctionService.findExpiredAuctions();

        assertNotNull(expiredAuctions);
        assertEquals(1, expiredAuctions.size());
        assertEquals(auction.getTitle(), expiredAuctions.getFirst().getTitle());
    }

    @Test
    void testAuctionToResponseDto() {
        when(productService.productToResponseDto(any())).thenReturn(new ProductResponseDto());
        when(userService.userToResponseDto(any())).thenReturn(new UserResponseDto());

        AuctionResponseDto responseDto = auctionService.auctionToResponseDto(auction);

        assertNotNull(responseDto);
        assertEquals(auction.getTitle(), responseDto.getTitle());
    }
}
