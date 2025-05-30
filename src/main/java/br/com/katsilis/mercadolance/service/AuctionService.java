package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateAuctionDto;
import br.com.katsilis.mercadolance.dto.response.AuctionBidResponseDto;
import br.com.katsilis.mercadolance.dto.response.AuctionResponseDto;
import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuctionService {

    List<AuctionResponseDto> findAll();
    Page<AuctionResponseDto> getAuctions(AuctionStatus status, String productName, Long creatorId, Pageable pageable);
    List<AuctionBidResponseDto> findByStatus(AuctionStatus status);
    AuctionBidResponseDto findById(Long id);
    AuctionResponseDto findByIdAndStatus(Long id, AuctionStatus status);
    Auction findOriginalByIdAndStatus(Long id, AuctionStatus status);
    Long create(CreateAuctionDto auction);
    void delete(Long id);
    void update(Long id, UpdateAuctionDto auction);
    List<Auction> findExpiredAuctions();
    AuctionResponseDto auctionToResponseDto(Auction auction);
    AuctionBidResponseDto auctionToResponseDto(Auction auction, double bidAmount);
}
