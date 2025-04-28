package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.BidDto;
import br.com.katsilis.mercadolance.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Map<String, String>> redisTemplate;
    private static final Duration TTL = Duration.ofSeconds(8);

    public void saveBidWithTtl(BidDto bid) {
        String key = buildBidKey(bid.getAuctionId(), bid.getUserId());
        Map<String, String> value = buildBidValue(bid.getAmount(), bid.getAuctionId(), bid.getUserId());
        redisTemplate.opsForHash().putAll(key, value);
        redisTemplate.expire(key, TTL);
    }

    private String buildBidKey(Long auctionId, Long userId) {
        return String.format("auction:%d:pendingBid:user:%d", auctionId, userId);
    }

    private Map<String, String> buildBidValue(Double amount, Long auctionId, Long userId) {
        return Map.of("amount", amount.toString(),
            "createdAt", LocalDateTime.now().toString(),
            "auctionId", auctionId.toString(),
            "userId", userId.toString());
    }

    public Set<String> getAuctionBidKeys(Long auctionId) {
        return redisTemplate.keys("auction:" + auctionId + ":pendingBid:user:*");
    }

    public Map<Object, Object> getAuctionBidValues(Long auctionId) {
        return redisTemplate.opsForHash().entries("auction:" + auctionId + ":pendingBid:user:*");
    }

    public void deleteBid(Long auctionId, Long userId) {
        redisTemplate.delete(buildBidKey(auctionId, userId));
    }
}
