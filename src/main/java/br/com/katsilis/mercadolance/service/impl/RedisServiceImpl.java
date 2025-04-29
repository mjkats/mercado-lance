package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofSeconds(6);

    @Override
    public void saveBidWithTtl(CreateBidDto bid) {
        String key = buildBidKey(bid.getAuctionId(), bid.getUserId());
        Map<String, String> value = buildBidValue(bid.getAmount(), bid.getAuctionId(), bid.getUserId());

        log.info("Saving bid with TTL, key: {}, value: {}", key, value);

        redisTemplate.opsForHash().putAll(key, value);
        redisTemplate.expire(key, TTL);

        log.info("Bid saved with TTL, key: {}", key);
    }

    private String buildBidKey(Long auctionId, Long userId) {
        return String.format("auction:%d:pendingBid:user:%d", auctionId, userId);
    }

    private Map<String, String> buildBidValue(Double amount, Long auctionId, Long userId) {
        return Map.of(
            "amount", amount.toString(),
            "createdAt", LocalDateTime.now().toString(),
            "auctionId", auctionId.toString(),
            "userId", userId.toString()
        );
    }

    @Override
    public Set<String> getAuctionBidKeys(Long auctionId) {
        log.info("Fetching bid keys for auctionId: {}", auctionId);

        Set<String> keys = redisTemplate.keys("auction:" + auctionId + ":pendingBid:user:*");

        log.info("Fetched bid keys for auctionId {}: {}", auctionId, keys);

        return keys;
    }

    @Override
    public List<Map<Object, Object>> getAuctionBidValues(Long auctionId) {
        log.info("Fetching bid values for auctionId: {}", auctionId);

        Set<String> keys = redisTemplate.keys("auction:" + auctionId + ":pendingBid:user:*");

        if (keys == null || keys.isEmpty()) {
            log.info("No bids found for auctionId: {}", auctionId);
            return List.of();
        }

        List<Map<Object, Object>> allBids = new ArrayList<>();
        for (String key : keys) {
            Map<Object, Object> bid = redisTemplate.opsForHash().entries(key);
            if (!bid.isEmpty()) {
                allBids.add(bid);
            }
        }

        log.info("Fetched bids for auctionId {}: {}", auctionId, allBids);

        return allBids;
    }

    @Override
    public void deleteBid(Long auctionId, Long userId) {
        try {
            String key = buildBidKey(auctionId, userId);
            log.info("Deleting bid with key: {}", key);

            redisTemplate.delete(key);
            log.info("Successfully deleted bid with key: {}", key);
        } catch (Exception e) {
            log.error("Cache deletion error for auctionId: {} and userId: {}, resuming application flow: ", auctionId, userId, e);
        }
    }
}