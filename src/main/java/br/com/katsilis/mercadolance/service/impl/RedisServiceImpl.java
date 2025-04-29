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

    public void saveBidWithTtl(CreateBidDto bid) {
        String key = buildBidKey(bid.getAuctionId(), bid.getUserId());
        Map<String, String> value = buildBidValue(bid.getAmount(), bid.getAuctionId(), bid.getUserId());
        redisTemplate.opsForHash().putAll(key, value);
        redisTemplate.expire(key, TTL);
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

    public Set<String> getAuctionBidKeys(Long auctionId) {
        return redisTemplate.keys("auction:" + auctionId + ":pendingBid:user:*");
    }

    @Override
    public List<Map<Object, Object>> getAuctionBidValues(Long auctionId) {
        Set<String> keys = redisTemplate.keys("auction:" + auctionId + ":pendingBid:user:*");

        if (keys == null || keys.isEmpty())
            return List.of();

        List<Map<Object, Object>> allBids = new ArrayList<>();
        for (String key : keys) {
            Map<Object, Object> bid = redisTemplate.opsForHash().entries(key);
            if (!bid.isEmpty()) {
                allBids.add(bid);
            }
        }

        return allBids;
    }

    public void deleteBid(Long auctionId, Long userId) {
        try {
            redisTemplate.delete(buildBidKey(auctionId, userId));
        } catch (Exception e) {
            log.error("Cache deletion error: ", e);
        }
    }
}