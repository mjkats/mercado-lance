package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.dto.response.BidResponseDto;
import br.com.katsilis.mercadolance.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface BidService {
    List<BidResponseDto> findAll();
    Page<BidResponseDto> getBids(Long auctionId, Long userId, Pageable pageable);
    BidResponseDto findById(Long id);
    Bid findOriginalById(Long id);
    void create(CreateBidDto bid);
    void delete(Long id);
    BidResponseDto getLatestActiveAuctionBid(Long auctionId);
    BidResponseDto bidToResponseDto(Bid bid);
    void handleBidUpdates(SseEmitter emitter, Long auctionId);
}
