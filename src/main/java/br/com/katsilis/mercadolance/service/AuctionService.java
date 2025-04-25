package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuctionService {

    List<Auction> findAll();
    Page<Auction> getAuctions(AuctionStatus status, String productName, Pageable pageable);
    List<Auction> findByStatus(AuctionStatus status);
    Auction findById(Long id);
    Auction save(Auction auction);
    void delete(Long id);
    Auction update(Long id, Auction auction);
}
