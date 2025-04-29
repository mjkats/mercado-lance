package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.model.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BidService {
    List<Bid> findAll();
    Page<Bid> getBids(Long auctionId, Long userId, Pageable pageable);
    Bid findById(Long id);
    void create(CreateBidDto bid);
    void delete(Long id);
    Bid getLatestActiveAuctionBid(Long auctionId);
}
