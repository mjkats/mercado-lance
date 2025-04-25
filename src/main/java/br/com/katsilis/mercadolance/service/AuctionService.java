package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuctionService {

    public List<Auction> findAll();
    public Page<Auction> getAuctions(AuctionStatus status, String productName, Pageable pageable);
    public List<Auction> findByStatus(AuctionStatus status);
    public Auction findById(Long id);
    public Auction save(Auction auction);
    public void delete(Long id);
    public Auction update(Long id, Auction auction);
}
