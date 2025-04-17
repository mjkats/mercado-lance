package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
