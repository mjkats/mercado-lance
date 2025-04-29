package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateAuctionDto;
import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
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
    Auction findByIdAndStatus(Long id, AuctionStatus status);
    Auction create(CreateAuctionDto auction);
    void delete(Long id);
    void update(Long id, UpdateAuctionDto auction);
    List<Auction> findExpiredAuctions();
}
