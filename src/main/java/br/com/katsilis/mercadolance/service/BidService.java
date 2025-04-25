package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.enums.BidStatus;
import br.com.katsilis.mercadolance.model.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BidService {
    List<Bid> findAll();
    Page<Bid> getBids(Long auctionId, Long userId, BidStatus status, Pageable pageable);
    Bid findById(Long id);
    Bid save(Bid bid);
    void delete(Long id);
    Bid update(Long id, Bid bid);
}
