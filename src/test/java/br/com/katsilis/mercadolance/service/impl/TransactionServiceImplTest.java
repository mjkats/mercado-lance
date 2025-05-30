package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateTransactionDto;
import br.com.katsilis.mercadolance.dto.response.BidResponseDto;
import br.com.katsilis.mercadolance.dto.response.TransactionResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.TransactionNotFoundException;
import br.com.katsilis.mercadolance.entity.Bid;
import br.com.katsilis.mercadolance.entity.Transaction;
import br.com.katsilis.mercadolance.entity.User;
import br.com.katsilis.mercadolance.repository.TransactionRepository;
import br.com.katsilis.mercadolance.service.BidService;
import br.com.katsilis.mercadolance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BidService bidService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;
    private TransactionResponseDto transactionResponseDto;
    private CreateTransactionDto createTransactionDto;

    @BeforeEach
    void setUp() {
        transaction = new Transaction(1L, new Bid(), new User(), 100.0, LocalDateTime.now(), LocalDateTime.now());
        transactionResponseDto = new TransactionResponseDto(new BidResponseDto(), new UserResponseDto(), 100.0, LocalDateTime.now());
        createTransactionDto = new CreateTransactionDto(1L, 1L, 100.0);
    }

    @Test
    void testFindAll_Success() {
        BidResponseDto bidResponseDto = new BidResponseDto();
        UserResponseDto userResponseDto = new UserResponseDto();

        when(bidService.bidToResponseDto(any(Bid.class))).thenReturn(bidResponseDto);
        when(userService.userToResponseDto(any(User.class))).thenReturn(userResponseDto);
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionResponseDto> result = transactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(transactionResponseDto.getWinningBid(), result.getFirst().getWinningBid());
        assertEquals(transactionResponseDto.getBuyer(), result.getFirst().getBuyer());
        assertEquals(transactionResponseDto.getAmount(), result.getFirst().getAmount(), 0.0);
    }

    @Test
    void testFindById_TransactionNotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.findById(1L));
    }

    @Test
    void testCreate_Success() {
        when(bidService.findOriginalById(1L)).thenReturn(new Bid());
        when(userService.findOriginalById(1L)).thenReturn(new User());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.create(createTransactionDto);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreate_Failure() {
        when(bidService.findOriginalById(1L)).thenThrow(new RuntimeException("Bid not found"));

        assertThrows(DatabaseException.class, () -> transactionService.create(createTransactionDto));
    }

    @Test
    void testDeleteById_Success() {
        doNothing().when(transactionRepository).deleteById(1L);

        transactionService.deleteById(1L);

        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_Failure() {
        doThrow(new RuntimeException("Error")).when(transactionRepository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> transactionService.deleteById(1L));
    }

    @Test
    void testTransactionToResponseDto() {
        BidResponseDto bidResponseDto = new BidResponseDto();
        UserResponseDto userResponseDto = new UserResponseDto();

        when(bidService.bidToResponseDto(any(Bid.class))).thenReturn(bidResponseDto);
        when(userService.userToResponseDto(any(User.class))).thenReturn(userResponseDto);

        TransactionResponseDto result = transactionService.transactionToResponseDto(transaction);

        assertNotNull(result);

        assertEquals(bidResponseDto, result.getWinningBid());
        assertEquals(userResponseDto, result.getBuyer());
        assertEquals(transaction.getAmount(), result.getAmount(), 0.0);

        assertTrue(Math.abs(transaction.getTransactionDate().toEpochSecond(ZoneOffset.UTC) - result.getTransactionDate().toEpochSecond(ZoneOffset.UTC)) < 1);
    }
}
