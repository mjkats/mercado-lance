package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.BidDto;

import java.util.Map;
import java.util.Set;

public interface RedisService {
    void saveBidWithTtl(BidDto bid);
    Set<String> getAuctionBidKeys(Long auctionId);
    Map<Object, Object> getAuctionBidValues(Long auctionId);
    void deleteBid(Long auctionId, Long userId);
}
