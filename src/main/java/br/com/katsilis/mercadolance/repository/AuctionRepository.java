package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
    Page<Auction> findByStatusAndProduct_NameContainingIgnoreCase(AuctionStatus status, String productName, Pageable pageable);
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);
    Page<Auction> findByProduct_NameContainingIgnoreCase(String productName, Pageable pageable);
    Optional<Auction> findByIdAndStatus(Long id, AuctionStatus status);
}
