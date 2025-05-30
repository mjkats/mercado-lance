package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Page<Bid> findByAuction_IdAndUser_Id(Long auctionId, Long userId, Pageable pageable);
    Page<Bid> findByAuction_Id(Long auctionId, Pageable pageable);
    List<Bid> findByAuction_Id(Long auctionId);
    Page<Bid> findByUser_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "auction", "auction.product", "auction.createdBy"})
    Optional<Bid> findTop1ByAuction_IdAndAuction_StatusOrderByAmountDesc(Long auctionId, AuctionStatus status);


}
