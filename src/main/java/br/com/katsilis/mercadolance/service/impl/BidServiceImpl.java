package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
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
    public Page<Bid> getBids(Long auctionId, Long userId, Pageable pageable) {

        if (auctionId != null && userId != null)
            return bidRepository.findByAuction_IdAndUser_Id(auctionId, userId, pageable);

        if (userId != null)
            return bidRepository.findByUser_Id(userId, pageable);

        if (auctionId != null)
            return bidRepository.findByAuction_Id(auctionId, pageable);

        return bidRepository.findAll(pageable);
    }

    @Override
    public Bid findById(Long id) {
        return bidRepository.findById(id).orElseThrow(() ->
            new EntityNotFoundException("Bid with id " + id + " not found"));
    }

    @Override
    @Transactional
    public void create(CreateBidDto bid) {
        redisService.saveBidWithTtl(bid);

        long timeoutMillis = 6000;
        long endTime = System.currentTimeMillis() + timeoutMillis;

        try {
            while (System.currentTimeMillis() < endTime) {
                log.info("a");
                Set<String> bidKeys = redisService.getAuctionBidKeys(bid.getAuctionId());

                if (bidKeys == null || bidKeys.isEmpty())
                    throw new RedisException("No keys were found for auction " + bid.getAuctionId() + " while creating bid for user " + bid.getUserId() + ".");

                Map<String, String> earliestBid = getEarliestBidFromRedis(bid.getAuctionId());

                if (earliestBid == null)
                    throw new RedisException("No bids were found for auction " + bid.getAuctionId() + " while creating bid for user " + bid.getUserId() + ".");

                if (canProcessBidCreation(bid, earliestBid)) {
                    processBidCreation(bid);
                    return;
                }

                Thread.sleep(1000);
            }

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sistema ocupado, tente novamente em instantes.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno.");
        } catch (Exception e) {
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
    public Bid getLatestActiveAuctionBid(Long auctionId) {
        return bidRepository.findTopByAuction_IdAndAuction_StatusOrderByAmountDesc(auctionId, AuctionStatus.ACTIVE)
            .orElseThrow(() -> new IllegalArgumentException("Highest bid could not be found for auction " + auctionId));
    }

    private Map<String, String> getEarliestBidFromRedis(Long auctionId) {
        List<Map<Object, Object>> bidsRedisData = redisService.getAuctionBidValues(auctionId);
        Map<String, String> earliestBid = null;
        LocalDateTime earliestCreatedAt = LocalDateTime.MAX;

        for (Map<Object, Object> bidRedisData : bidsRedisData) {
            Map<String, String> bidData = new HashMap<>();
            for (Map.Entry<Object, Object> entry : bidRedisData.entrySet()) {
                bidData.put(entry.getKey().toString(), entry.getValue().toString());
            }

            String createdAtStr = bidData.get("createdAt");

            try {
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);

                if (createdAt.isBefore(earliestCreatedAt)) {
                    earliestCreatedAt = createdAt;
                    earliestBid = bidData;
                }

            } catch (Exception e) {
                log.error("Erro ao fazer parse de createdAt para o lance: {}", bidData, e);
            }
        }

        return earliestBid;
    }

    private boolean canProcessBidCreation(CreateBidDto bid, Map<String, String> earliestBid) {
        return bid.getUserId().equals(Long.parseLong(earliestBid.get("userId")))
            && bid.getAuctionId().equals(Long.parseLong(earliestBid.get("auctionId")));
    }

    private Bid processBidCreation(CreateBidDto bid) {
        Auction auction = auctionService.findByIdAndStatus(bid.getAuctionId(), AuctionStatus.ACTIVE);

        if (auction.getCreatedBy().getId().equals(bid.getUserId()))
            throw new IllegalArgumentException("You cannot bid on your own auction");

        if (auction.getStartingPrice() >= bid.getAmount())
            throw new IllegalArgumentException("Your bid must be higher than the starting price of the auction");

        if (auction.getBids() != null && !auction.getBids().isEmpty()) {
            Bid highestBid = auction.getBids().stream()
                .max(Comparator.comparing(Bid::getAmount))
                .orElseThrow(() -> new IllegalArgumentException("No bids found for auction"));

            if (highestBid.getUser().getId().equals(bid.getUserId()))
                throw new IllegalArgumentException("You already have the highest bid placed");

            if (highestBid.getAmount() >= bid.getAmount())
                throw new IllegalArgumentException("Your bid must be higher than the current highest bid");
        }

        User user = userService.findById(bid.getUserId());
        Bid newBid = new Bid(user, auction, bid.getAmount());
        return bidRepository.save(newBid);
    }
}
