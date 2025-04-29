package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateAuctionDto;
import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.model.Product;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.AuctionRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import br.com.katsilis.mercadolance.service.ProductService;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductService productService;
    private final UserService userService;

    @Override
    public List<Auction> findAll() {
        try {
            return auctionRepository.findAll();
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction findAll");
        }
    }

    @Override
    public Page<Auction> getAuctions(AuctionStatus status, String productName, Pageable pageable) {

        try {
            if (productName != null && status != null)
                return auctionRepository.findByStatusAndProduct_NameContainingIgnoreCase(status, productName, pageable);

            if (productName != null)
                return auctionRepository.findByProduct_NameContainingIgnoreCase(productName, pageable);

            if (status != null)
                return auctionRepository.findByStatus(status, pageable);

            return auctionRepository.findAll(pageable);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction getAuctions");
        }
    }

    @Override
    public List<Auction> findByStatus(AuctionStatus status) {
        try {
            return auctionRepository.findByStatus(status);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction findByStatus");
        }
    }

    @Override
    public Auction findById(Long id) {
        try {
            return auctionRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Auction with id " + id + " not found"));
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction findById");
        }
    }

    @Override
    public Auction findByIdAndStatus(Long id, AuctionStatus status) {
        try {
            return auctionRepository.findByIdAndStatus(id, status).orElseThrow(() ->
                new EntityNotFoundException("Auction with id " + id + " not found"));
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction findActiveById");
        }
    }

    @Override
    public Auction create(CreateAuctionDto auction) {
        LocalDateTime now = LocalDateTime.now();

        if (auction.getEndTime().isBefore(now))
            throw new IllegalArgumentException("Auction end time needs to be after current time");

        Product product = productService.findById(auction.getProductId());
        User creator = userService.findById(auction.getCreatorId());

        Auction newAuction = Auction.builder()
            .createdAt(now)
            .startTime(now)
            .title(auction.getTitle())
            .description(auction.getDescription())
            .startingPrice(auction.getStartingPrice())
            .endTime(auction.getEndTime())
            .product(product)
            .status(AuctionStatus.ACTIVE)
            .createdBy(creator)
            .build();

        try {
            return auctionRepository.save(newAuction);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction create");
        }
    }

    @Override
    public void delete(Long id) {
        if (!auctionRepository.existsById(id))
            throw new EntityNotFoundException("Auction with id " + id + " not found");

        try {
            auctionRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e.getMessage(), "Auction delete");
        }
    }

    @Override
    public void update(Long id, UpdateAuctionDto updatedAuction) {
        Auction existing = findById(id);
        boolean hasUpdate = false;

        if (updatedAuction.getTitle() != null) {
            existing.setTitle(updatedAuction.getTitle());
            hasUpdate = true;
        }

        if (updatedAuction.getDescription() != null) {
            existing.setDescription(updatedAuction.getDescription());
            hasUpdate = true;
        }

        if (updatedAuction.getEndTime() != null && updatedAuction.getEndTime().isAfter(existing.getEndTime())) {
            existing.setEndTime(updatedAuction.getEndTime());
            hasUpdate = true;
        }

        if (updatedAuction.getStatus() != null) {
            existing.setStatus(updatedAuction.getStatus());
            hasUpdate = true;
        }

        if (hasUpdate) {
            existing.setUpdatedAt(LocalDateTime.now());

            try {
                auctionRepository.save(existing);
                return;
            } catch (RuntimeException e) {
                throw new DatabaseException(e.getMessage(), "Auction update");
            }
        }

        throw new IllegalArgumentException("There are no updates to be made for auction id " + id + ".");
    }

    @Override
    public List<Auction> findExpiredAuctions() {
        return auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ACTIVE, LocalDateTime.now());
    }
}
