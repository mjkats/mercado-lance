package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.exception.handler.DatabaseException;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.repository.AuctionRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;

    @Override
    public List<Auction> findAll() {
        try {
            return auctionRepository.findAll();
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "findAll");
        }
    }

    @Override
    public Page<Auction> getAuctions(AuctionStatus status, String productName, Pageable pageable) {

        try {
            if (productName != null && status != null)
                return auctionRepository.findByStatusAndProductNamePaginated(status, productName, pageable);

            if (productName != null)
                return auctionRepository.findByProductNamePaginated(productName, pageable);

            if (status != null)
                return auctionRepository.findByStatusPaginated(status, pageable);

            return auctionRepository.findPaginated(pageable);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "getAuctions");
        }
    }

    @Override
    public List<Auction> findByStatus(AuctionStatus status) {
        try {
            return auctionRepository.findByStatus(status);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "findByStatus");
        }
    }

    @Override
    public Auction findById(Long id) {
        try {
            return auctionRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Auction with id " + id + " not found"));
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "findById");
        }
    }

    @Override
    public Auction save(Auction auction) {
        try {
            return auctionRepository.save(auction);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "save");
        }
    }

    @Override
    public void delete(Long id) {
        if (!auctionRepository.existsById(id))
            throw new EntityNotFoundException("Auction with id " + id + " not found");

        try {
            auctionRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "delete");
        }
    }

    @Override
    public Auction update(Long id, Auction updatedAuction) {
        Auction existing = findById(id);

        existing.setTitle(updatedAuction.getTitle());
        existing.setDescription(updatedAuction.getDescription());
        existing.setStartTime(updatedAuction.getStartTime());
        existing.setEndTime(updatedAuction.getEndTime());
        existing.setStatus(updatedAuction.getStatus());
        existing.setProduct(updatedAuction.getProduct());

        try {
            return auctionRepository.save(existing);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "update");
        }
    }
}
