package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    private CreateBidDto bidDto;

    @BeforeEach
    void setUp() {
        bidDto = new CreateBidDto();
        bidDto.setUserId(10L);
        bidDto.setAuctionId(5L);
        bidDto.setAmount(100.0);
    }

    @Test
    void testSaveBidWithTtl() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        redisService.saveBidWithTtl(bidDto);

        String expectedKey = "auction:5:pendingBid:user:10";
        ArgumentCaptor<Map<String, String>> valueCaptor = ArgumentCaptor.forClass(Map.class);

        verify(hashOperations).putAll(eq(expectedKey), valueCaptor.capture());
        verify(redisTemplate).expire(eq(expectedKey), eq(Duration.ofSeconds(6)));

        Map<String, String> captured = valueCaptor.getValue();
        assertEquals("100.0", captured.get("amount"));
        assertEquals("5", captured.get("auctionId"));
        assertEquals("10", captured.get("userId"));
        assertNotNull(captured.get("createdAt"));
    }

    @Test
    void testGetAuctionBidKeys() {
        Set<String> mockKeys = Set.of("auction:5:pendingBid:user:10", "auction:5:pendingBid:user:11");
        when(redisTemplate.keys("auction:5:pendingBid:user:*")).thenReturn(mockKeys);

        Set<String> result = redisService.getAuctionBidKeys(5L);

        assertEquals(mockKeys, result);
    }

    @Test
    void testGetAuctionBidValues_WithBids() {
        Set<String> keys = Set.of("auction:5:pendingBid:user:10");
        Map<Object, Object> bidMap = Map.of(
            "amount", "100.0",
            "auctionId", "5",
            "userId", "10",
            "createdAt", LocalDateTime.now().toString()
        );

        when(redisTemplate.keys("auction:5:pendingBid:user:*")).thenReturn(keys);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries("auction:5:pendingBid:user:10")).thenReturn(bidMap);

        List<Map<Object, Object>> result = redisService.getAuctionBidValues(5L);

        assertEquals(1, result.size());
        assertEquals(bidMap, result.getFirst());
    }

    @Test
    void testGetAuctionBidValues_NoBids() {
        when(redisTemplate.keys("auction:5:pendingBid:user:*")).thenReturn(Set.of());

        List<Map<Object, Object>> result = redisService.getAuctionBidValues(5L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteBid() {
        redisService.deleteBid(5L, 10L);

        verify(redisTemplate).delete("auction:5:pendingBid:user:10");
    }
}
