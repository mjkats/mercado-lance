package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.BidDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.enums.BidStatus;
import br.com.katsilis.mercadolance.exception.RedisException;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import br.com.katsilis.mercadolance.service.BidService;
import br.com.katsilis.mercadolance.service.RedisService;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final UserService userService;
    private final RedisService redisService;

    @Override
    public List<Bid> findAll() {
        return bidRepository.findAll();
    }

    @Override
    public Page<Bid> getBids(Long auctionId, Long userId, BidStatus status, Pageable pageable) {

        if (auctionId != null && userId != null && status != null)
            return bidRepository.findByAuction_IdAndUser_IdAndBidStatus(auctionId, userId, status, pageable);

        if (auctionId != null && userId != null)
            return bidRepository.findByAuction_IdAndUser_Id(auctionId, userId, pageable);

        if (auctionId != null && status != null)
            return bidRepository.findByAuction_IdAndBidStatus(auctionId, status, pageable);

        if (userId != null && status != null)
            return bidRepository.findByUser_IdAndBidStatus(userId, status, pageable);

        if (userId != null)
            return bidRepository.findByUser_Id(userId, pageable);

        if (auctionId != null)
            return bidRepository.findByAuction_Id(auctionId, pageable);

        if (status != null)
            return bidRepository.findByBidStatus(status, pageable);

        return bidRepository.findAll(pageable);
    }

    @Override
    public Bid findById(Long id) {
        return bidRepository.findById(id).orElseThrow(() ->
            new EntityNotFoundException("Bid with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Bid create(BidDto bid) {
        redisService.saveBidWithTtl(bid);

        long timeoutMillis = 6000;
        long endTime = System.currentTimeMillis() + timeoutMillis;

        try {
            while (System.currentTimeMillis() < endTime) {
                Set<String> bidKeys = redisService.getAuctionBidKeys(bid.getAuctionId());

                if (bidKeys == null || bidKeys.isEmpty())
                    throw new RedisException("No keys were found for auction " + bid.getAuctionId() + " while creating bid for user " + bid.getUserId() + ".");

                Map<String, String> earliestBid = getEarliestBidFromRedis(bid.getAuctionId());

                if (earliestBid == null)
                    throw new RedisException("No bids were found for auction " + bid.getAuctionId() + " while creating bid for user " + bid.getUserId() + ".");

                if (canProcessBidCreation(bid, earliestBid))
                    return processBidCreation(bid);

                Thread.sleep(1000);
            }

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sistema ocupado, tente novamente em instantes.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno.");
        } finally {
            redisService.deleteBid(bid.getAuctionId(), bid.getUserId());
        }
    }

    @Override
    public void delete(Long id) {
        if (!bidRepository.existsById(id))
            throw new EntityNotFoundException("Bid with id " + id + " not found");

        bidRepository.deleteById(id);
    }

    @Override
    public Bid update(Long id, Bid bid) {
        Bid existing = findById(id);
        existing.setBidTime(bid.getBidTime());
        existing.setAmount(bid.getAmount());
        existing.setStatus(bid.getStatus());

        return bidRepository.save(existing);
    }

    @Override
    public Bid getLatestAuctionBid(Long auctionId) {
        return bidRepository.findTopByAuction_IdAndStatusOrderByAmountDesc(auctionId, BidStatus.ACCEPTED)
            .orElseThrow(() -> new IllegalArgumentException("Highest bid could not be found for auction " + auctionId));
    }

    private Map<String, String> getEarliestBidFromRedis(Long auctionId) {
        Map<Object, Object> bidsRedisData = redisService.getAuctionBidValues(auctionId);
        Map<String, String> earliestBid = null;
        LocalDateTime earliestCreatedAt = LocalDateTime.MAX;

        for (Map.Entry<Object, Object> entry : bidsRedisData.entrySet()) {
            Map<String, String> bidRedisData = (Map<String, String>) entry.getValue();
            String createdAtStr = bidRedisData.get("createdAt");

            try {
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);

                if (createdAt.isBefore(earliestCreatedAt)) {
                    earliestCreatedAt = createdAt;
                    earliestBid = bidRedisData;
                }

            } catch (Exception e) {
                log.error("Erro ao fazer parse de createdAt para o lance: {}", entry.getKey(), e);
            }
        }
        return earliestBid;
    }

    private boolean canProcessBidCreation(BidDto bid, Map<String, String> earliestBid) {
        return bid.getUserId().equals(Long.parseLong(earliestBid.get("userId")))
            && bid.getAuctionId().equals(Long.parseLong(earliestBid.get("auctionId")));
    }

    private Bid processBidCreation(BidDto bid) {
        Auction auction = auctionService.findByIdAndStatus(bid.getAuctionId(), AuctionStatus.ACTIVE);

        Bid highestBid = auction.getBids().stream()
            .max(Comparator.comparing(Bid::getAmount))
            .orElseThrow(() -> new IllegalArgumentException("No bids found for auction"));

        if (highestBid.getUser().getId().equals(bid.getUserId()))
            throw new IllegalArgumentException("You cannot bid on your own auction");

        if (highestBid.getAmount() >= bid.getAmount())
            throw new IllegalArgumentException("Your bid must be higher than the current highest bid");

        User user = userService.findById(bid.getUserId());
        Bid newBid = new Bid(user, auction, bid.getAmount());
        return bidRepository.save(newBid);
    }
}
