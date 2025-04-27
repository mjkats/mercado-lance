package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.model.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Page<Bid> findByAuction_IdAndUser_Id(Long auctionId, Long userId, Pageable pageable);
    Page<Bid> findByAuction_Id(Long auctionId, Pageable pageable);
    Page<Bid> findByUser_Id(Long userId, Pageable pageable);
    Optional<Bid> findTopByAuction_IdOrderByAmountDesc(Long auctionId);
}
