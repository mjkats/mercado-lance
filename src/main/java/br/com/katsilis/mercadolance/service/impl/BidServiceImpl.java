package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.dto.response.AuctionResponseDto;
import br.com.katsilis.mercadolance.dto.response.BidResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.exception.*;
import br.com.katsilis.mercadolance.exception.illegalargument.BidIllegalArgumentException;
import br.com.katsilis.mercadolance.exception.notfound.BidNotFoundException;
import br.com.katsilis.mercadolance.exception.notfound.RedisCacheMissException;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.AuctionService;
import br.com.katsilis.mercadolance.service.BidService;
import br.com.katsilis.mercadolance.service.RedisService;
import br.com.katsilis.mercadolance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<BidResponseDto> findAll() {
        log.info("Fetching all bids");

        try {
            List<Bid> bids = bidRepository.findAll();
            List<BidResponseDto> response = bids.stream().map(this::bidToResponseDto).toList();

            log.info("Fetched bids: {}", bids);
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching all bids", e);
        }
    }

    @Override
    public Page<BidResponseDto> getBids(Long auctionId, Long userId, Pageable pageable) {
        log.info("Fetching bids with filters - auctionId: {}, userId: {}", auctionId, userId);

        try {
            Page<BidResponseDto> response = findBidsByFilters(auctionId, userId, pageable).map(this::bidToResponseDto);
            log.info("Fetched bids with filters: {}", response.getContent());
            return response;
        } catch (Exception e) {
            throw new DatabaseException("Error while searching for bids with filters", e);
        }
    }

    private Page<Bid> findBidsByFilters(Long auctionId, Long userId, Pageable pageable) {
        if (auctionId != null && userId != null)
            return bidRepository.findByAuction_IdAndUser_Id(auctionId, userId, pageable);

        if (userId != null)
            return bidRepository.findByUser_Id(userId, pageable);

        if (auctionId != null)
            return bidRepository.findByAuction_Id(auctionId, pageable);

        return bidRepository.findAll(pageable);
    }

    @Override
    public BidResponseDto findById(Long id) {
        log.info("Fetching bid by id: {}", id);

        try {
            Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new BidNotFoundException("Bid with id " + id + " not found"));

            BidResponseDto response = bidToResponseDto(bid);
            log.info("Fetched bid: {}", bid);
            return response;
        } catch (BidNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching bid with id " + id, e);
        }
    }

    @Override
    public Bid findOriginalById(Long id) {
        log.info("Fetching original bid by id: {}", id);

        try {
            Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new BidNotFoundException("Bid with id " + id + " not found"));

            log.info("Fetched original bid: {}", bid);
            return bid;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching bid with id " + id, e);
        }
    }

    @Override
    @Transactional
    public void create(CreateBidDto bid) {
        log.info("Creating new bid with data: {}", bid);

        try {
            redisService.saveBidWithTtl(bid);
            processBidWithTimeout(bid);
            log.info("Successfully created bid with data: {}", bid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThreadException("Thread interrupted while creating bid", e);
        } finally {
            redisService.deleteBid(bid.getAuctionId(), bid.getUserId());
        }
    }

    private void processBidWithTimeout(CreateBidDto bid) throws InterruptedException {
        long timeoutMillis = 6000;
        long endTime = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < endTime) {
            if (tryProcessBid(bid)) return;
            Thread.sleep(1000);
        }

        throw new BidWaitPeriodException("Bid could not be placed on auction " + bid.getAuctionId() + " due to many bids taking place on this auction. Try again in a moment.");
    }

    private boolean tryProcessBid(CreateBidDto bid) {
        Set<String> bidKeys = redisService.getAuctionBidKeys(bid.getAuctionId());

        if (bidKeys == null || bidKeys.isEmpty())
            throw new RedisCacheMissException("No keys found for auction " + bid.getAuctionId());

        Map<String, String> earliestBid = getEarliestBidFromRedis(bid.getAuctionId());

        if (earliestBid == null)
            throw new RedisCacheMissException("No bid values found for auction key values from auction id " + bid.getAuctionId());

        if (canProcessBidCreation(bid, earliestBid)) {
            processBidCreation(bid);
            return true;
        }

        return false;
    }

    private Map<String, String> getEarliestBidFromRedis(Long auctionId) {
        List<Map<Object, Object>> bidsRedisData = redisService.getAuctionBidValues(auctionId);
        Map<String, String> earliestBid = null;
        LocalDateTime earliestCreatedAt = LocalDateTime.MAX;

        for (Map<Object, Object> bidRedisData : bidsRedisData) {
            Map<String, String> bidData = new HashMap<>();

            for (Map.Entry<Object, Object> entry : bidRedisData.entrySet())
                bidData.put(entry.getKey().toString(), entry.getValue().toString());

            String createdAtStr = bidData.get("createdAt");

            try {
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);

                if (createdAt.isBefore(earliestCreatedAt)) {
                    earliestCreatedAt = createdAt;
                    earliestBid = bidData;
                }
            } catch (Exception e) {
                throw new JsonParsingException("Error while parsing createdAt to LocalDateTime for bid: " + bidData, e);
            }
        }

        log.info("Earliest bid found: {}", earliestBid);
        return earliestBid;
    }

    private boolean canProcessBidCreation(CreateBidDto bid, Map<String, String> earliestBid) {
        return bid.getUserId().equals(Long.parseLong(earliestBid.get("userId")))
            && bid.getAuctionId().equals(Long.parseLong(earliestBid.get("auctionId")));
    }

    private void processBidCreation(CreateBidDto bid) {
        log.info("Processing bid creation for bid: {}", bid);

        Auction auction = auctionService.findOriginalByIdAndStatus(bid.getAuctionId(), AuctionStatus.ACTIVE);

        if (auction.getCreatedBy().getId().equals(bid.getUserId()))
            throw new BidIllegalArgumentException("User " + bid.getUserId() + " cannot bid on his own auction");

        if (auction.getStartingPrice() >= bid.getAmount())
            throw new BidIllegalArgumentException("User's " + bid.getUserId() + " bid must be higher than the starting price of the auction");

        List<Bid> auctionBids;
        try {
            auctionBids = bidRepository.findByAuction_Id(auction.getId());
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching bids for auction " + auction.getId(), e);
        }

        if (auctionBids != null && !auctionBids.isEmpty()) {
            Bid highestBid = auctionBids.stream()
                .max(Comparator.comparing(Bid::getAmount))
                .orElseThrow(() -> new BidNotFoundException("Auction " + auction.getId() + " has no bids yet"));

            if (highestBid.getUser().getId().equals(bid.getUserId())) {
                throw new BidIllegalArgumentException("User " + bid.getUserId() + " already has the highest bid placed");
            }

            if (highestBid.getAmount() >= bid.getAmount()) {
                throw new BidIllegalArgumentException("User's " + bid.getUserId() + " bid must be higher than the current highest bid");
            }
        }

        try {
            User user = userService.findOriginalById(bid.getUserId());
            Bid newBid = new Bid(user, auction, bid.getAmount());
            bidRepository.save(newBid);
            log.info("Successfully created bid: {}", newBid);
        } catch (Exception e) {
            throw new DatabaseException("Error while creating bid", e);
        }
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting bid with id: {}", id);

        try {
            if (!bidRepository.existsById(id)) {
                throw new BidNotFoundException("Bid with id " + id + " not found");
            }

            bidRepository.deleteById(id);
            log.info("Successfully deleted bid with id: {}", id);
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting bid with id " + id, e);
        }
    }

    @Override
    public BidResponseDto getLatestActiveAuctionBid(Long auctionId) {
        log.info("Fetching latest active auction bid for auctionId: {}", auctionId);

        try {
            Bid bid = bidRepository.findTopByAuction_IdAndAuction_StatusOrderByAmountDesc(auctionId, AuctionStatus.ACTIVE)
                .orElseThrow(() -> new BidNotFoundException("Highest bid could not be found for auction " + auctionId));

            BidResponseDto response = bidToResponseDto(bid);
            log.info("Fetched latest active auction bid: {}", bid);
            return response;
        } catch (BidNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error while fetching latest active auction bid from auction with id " + auctionId, e);
        }
    }

    @Override
    public BidResponseDto bidToResponseDto(Bid bid) {
        UserResponseDto userResponseDto = userService.userToResponseDto(bid.getUser());
        AuctionResponseDto auctionResponseDto = auctionService.auctionToResponseDto(bid.getAuction());

        return new BidResponseDto(userResponseDto, auctionResponseDto, bid.getAmount());
    }
}