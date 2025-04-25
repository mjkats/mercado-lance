package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
    Page<Auction> findByStatusAndProductNamePaginated(AuctionStatus status, String productName, Pageable pageable);
    Page<Auction> findByStatusPaginated(AuctionStatus status, Pageable pageable);
    Page<Auction> findByProductNamePaginated(String productName, Pageable pageable);
    Page<Auction> findPaginated(Pageable pageable);
}
