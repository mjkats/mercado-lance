package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    void saveBidWithTtl(CreateBidDto bid);
    Set<String> getAuctionBidKeys(Long auctionId);
    List<Map<Object, Object>> getAuctionBidValues(Long auctionId);
    void deleteBid(Long auctionId, Long userId);
}