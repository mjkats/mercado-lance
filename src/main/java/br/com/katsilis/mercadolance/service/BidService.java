package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.BidDto;
import br.com.katsilis.mercadolance.model.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BidService {
    List<Bid> findAll();
    Page<Bid> getBids(Long auctionId, Long userId, Pageable pageable);
    Bid findById(Long id);
    Bid create(BidDto bid);
    void delete(Long id);
    Bid update(Long id, Bid bid);
    Bid getLatestAuctionBid(Long auctionId);
}
