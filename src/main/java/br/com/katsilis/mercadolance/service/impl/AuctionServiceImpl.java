package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateAuctionDto;
import br.com.katsilis.mercadolance.dto.creation.CreateNotificationDto;
import br.com.katsilis.mercadolance.dto.response.*;
import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.exception.illegalargument.AuctionIllegalArgumentException;
import br.com.katsilis.mercadolance.exception.notfound.AuctionNotFoundException;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.entity.Auction;
import br.com.katsilis.mercadolance.entity.Bid;
import br.com.katsilis.mercadolance.entity.Product;
import br.com.katsilis.mercadolance.entity.User;
import br.com.katsilis.mercadolance.repository.AuctionRepository;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import br.com.katsilis.mercadolance.service.NotificationService;
import br.com.katsilis.mercadolance.service.ProductService;
import br.com.katsilis.mercadolance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProductService productService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public List<AuctionResponseDto> findAll() {
        log.info("Fetching all auctions");

        try {
            List<Auction> auctions = auctionRepository.findAll();
            log.info("Fetched auctions from database: {}", auctions);

            return auctions.stream().map(this::auctionToResponseDto).toList();
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching all auctions", e);
        }
    }

    @Override
    public Page<AuctionResponseDto> getAuctions(AuctionStatus status, String productName, Long userId, Pageable pageable) {
        log.info("Called getAuctions with status={} and productName={}", status, productName);

        try {
            Page<AuctionResponseDto> result;

            if (productName != null && status != null) {
                result = auctionRepository.findByStatusAndProduct_NameContainingIgnoreCase(status, productName, pageable)
                    .map(this::auctionToResponseDto);
            } else if (userId != null && status != null) {
                result = auctionRepository.findByStatusAndCreatedBy_Id(status, userId, pageable)
                    .map(this::auctionToResponseDto);
            } else if (userId != null) {
                result = auctionRepository.findByCreatedBy_Id(userId, pageable)
                    .map(this::auctionToResponseDto);
            } else if (productName != null) {
                result = auctionRepository.findByProduct_NameContainingIgnoreCase(productName, pageable)
                    .map(this::auctionToResponseDto);
            } else if (status != null) {
                result = auctionRepository.findByStatus(status, pageable)
                    .map(this::auctionToResponseDto);
            } else {
                result = auctionRepository.findAll(pageable).map(this::auctionToResponseDto);
            }

            log.info("Returning getAuctions result: {}", result.getContent());
            return result;

        } catch (Exception e) {
            throw new DatabaseException("Error while searching for auctions with filters", e);
        }
    }

    @Override
    public List<AuctionBidResponseDto> findByStatus(AuctionStatus status) {
        log.info("Called findByStatus with status={}", status);

        try {
            List<AuctionBidResponseDto> response = new ArrayList<>();
            List<Auction> auctions = auctionRepository.findByStatus(status);

            auctions.forEach(auc -> response.add(auctionToResponseDto(auc, getAuctionHighestBidAmount(auc))));

            log.info("Returning findByStatus result: {}", response);
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while searching for auctions by status", e);
        }
    }

    @Override
    public AuctionBidResponseDto findById(Long id) {
        log.info("Called findById with id={}", id);

        try {
            Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction with id " + id + " not found"));

            AuctionBidResponseDto response = auctionToResponseDto(auction, getAuctionHighestBidAmount(auction));
            log.info("Returning findById result: {}", response);
            return response;
        } catch (AuctionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching auction with id " + id, e);
        }
    }

    @Override
    public AuctionResponseDto findByIdAndStatus(Long id, AuctionStatus status) {
        log.info("Called findByIdAndStatus with id={} and status={}", id, status);

        try {
            Auction auction = auctionRepository.findByIdAndStatus(id, status)
                .orElseThrow(() -> new AuctionNotFoundException("Auction with id " + id + " not found"));

            AuctionResponseDto response = auctionToResponseDto(auction);
            log.info("Returning findByIdAndStatus result: {}", response);
            return response;
        } catch (AuctionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching auction with id " + id + " by id and status", e);
        }
    }

    @Override
    public Auction findOriginalByIdAndStatus(Long id, AuctionStatus status) {
        log.info("Called findOriginalByIdAndStatus with id={} and status={}", id, status);

        try {
            Auction auction = auctionRepository.findByIdAndStatus(id, status)
                .orElseThrow(() -> new AuctionNotFoundException("Auction with id " + id + " not found"));

            log.info("Returning findOriginalByIdAndStatus result: {}", auction);
            return auction;
        } catch (AuctionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching auction with id " + id + " by id and status", e);
        }
    }

    @Override
    public Long create(CreateAuctionDto auction) {
        log.info("Processing auction creation: {}", auction);

        if (auction.getEndTime().isBefore(LocalDateTime.now()))
            throw new AuctionIllegalArgumentException(
                "Auction end time needs to be after current time",
                "A data de término está inválida"
            );

        try {
            Product product = productService.findOriginalById(auction.getProductId());
            User creator = userService.findOriginalById(auction.getCreatorId());

            Auction newAuction = Auction.builder()
                .createdAt(LocalDateTime.now())
                .startTime(LocalDateTime.now())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startingPrice(auction.getStartingPrice())
                .endTime(auction.getEndTime())
                .product(product)
                .status(AuctionStatus.ACTIVE)
                .createdBy(creator)
                .build();

            Auction savedAuction = auctionRepository.save(newAuction);
            log.info("Finished create. Saved auction: {}", newAuction);
            return savedAuction.getId();
        } catch (Exception e) {
            throw new DatabaseException("Error while creating auction", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Called delete with id={}", id);

        try {
            Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction with id " + id + " not found"));

            if (!bidRepository.findByAuction_Id(id).isEmpty())
                bidRepository.deleteByAuctionId(id);

            auctionRepository.delete(auction);
            log.info("Finished delete. Auction with id {} deleted", id);

        } catch (AuctionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting auction with id " + id, e);
        }
    }

    @Override
    public void update(Long id, UpdateAuctionDto updatedAuction) {
        log.info("Called update with id={} and updatedAuction={}", id, updatedAuction);

        try {
            Auction existing = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction with id " + id + " not found"));

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

                if (existing.getStatus().equals(AuctionStatus.ACTIVE) && updatedAuction.getStatus().equals(AuctionStatus.FINISHED)) {
                    Bid highestAuctionBid = getAuctionHighestBid(existing);

                    if (highestAuctionBid != null)
                        notificationService.create(new CreateNotificationDto(highestAuctionBid.getAuction().getCreatedBy().getId(), "Your bid of " + highestAuctionBid.getAmount() + " won the auction " + existing.getTitle()));
                }


                existing.setStatus(updatedAuction.getStatus());
                hasUpdate = true;
            }

            if (!hasUpdate)
                throw new AuctionIllegalArgumentException(
                    "There are no updates on the request for auction id " + id,
                    "Não existem modificações no leilão"
                );

            existing.setUpdatedAt(LocalDateTime.now());
            auctionRepository.save(existing);
            log.info("Finished update. Updated auction: {}", existing);

        } catch (AuctionNotFoundException | AuctionIllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while updating Auction with id " + id, e);
        }
    }

    @Override
    public List<Auction> findExpiredAuctions() {
        log.info("Fetching expired auctions");

        try {
            List<Auction> expiredAuctions = auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ACTIVE, LocalDateTime.now());
            log.info("Found {} expired auctions", expiredAuctions.size());
            return expiredAuctions;
        } catch (Exception e) {
            log.error("Error while fetching expired auctions", e);
            throw new DatabaseException("Error while fetching expired auctions", e);
        }
    }

    @Override
    public AuctionResponseDto auctionToResponseDto(Auction auction) {
        ProductResponseDto productResponseDto = productService.productToResponseDto(auction.getProduct());
        UserResponseDto userResponseDto = userService.userToResponseDto(auction.getCreatedBy());

        return new AuctionResponseDto(
            auction.getId(),
            auction.getTitle(),
            auction.getDescription(),
            productResponseDto,
            userResponseDto,
            auction.getStartingPrice(),
            auction.getStartTime(),
            auction.getEndTime(),
            auction.getStatus()
        );
    }

    @Override
    public AuctionBidResponseDto auctionToResponseDto(Auction auction, double bidAmount) {
        ProductResponseDto productResponseDto = productService.productToResponseDto(auction.getProduct());
        UserResponseDto userResponseDto = userService.userToResponseDto(auction.getCreatedBy());

        return new AuctionBidResponseDto(
            auction.getId(),
            auction.getTitle(),
            auction.getDescription(),
            productResponseDto,
            userResponseDto,
            auction.getStartingPrice(),
            auction.getStartTime(),
            auction.getEndTime(),
            auction.getStatus(),
            bidAmount
        );
    }

    private double getAuctionHighestBidAmount(Auction auction) {
        return bidRepository
            .findTop1ByAuction_IdAndAuction_StatusOrderByAmountDesc(auction.getId(), AuctionStatus.ACTIVE)
            .map(Bid::getAmount)
            .orElse(auction.getStartingPrice());
    }

    private Bid getAuctionHighestBid(Auction auction) {
        return bidRepository
            .findTop1ByAuction_IdAndAuction_StatusOrderByAmountDesc(auction.getId(), AuctionStatus.ACTIVE).orElse(null);
    }
}
